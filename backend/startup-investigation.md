# Render Startup Investigation

## Evidence

Reported timings:

| Phase | Time |
| --- | --- |
| Spring Boot start to Hikari starting | 44 seconds |
| Hikari starting to first PostgreSQL connection | 8 seconds |
| First PostgreSQL connection to application started | 64 seconds |

The timestamps span 116 seconds, not 126.02. The supplied first log may occur after
Spring's startup measurement begins; full logs are needed to reconcile the difference.
These figures do not include all possible platform provisioning time.

The dependency set is Spring Boot 3.5.7 / Java 21 with MVC, Security, JPA,
Validation, PostgreSQL and JJWT. There is no Actuator, DevTools, migration engine,
or extra database driver. Eight entities and eight repositories are present.
There are no CommandLineRunner / ApplicationRunner data-loading jobs.
Password reset startup hooks validate configuration; they do not send email.

The first connection may include DNS, TLS, authentication, network latency and a
database wake-up. The supplied timestamps cannot separate those costs. Most time
falls outside that first connection: class loading, context creation, Hibernate
mapping/schema validation, repository creation and remaining beans are plausible
contributors under a constrained CPU allocation. This is a hypothesis, not a
profiled diagnosis. Eager entity relationships do not load rows just by booting JPA.

## Conservative Change

Hikari's implicit defaults maintain up to ten idle connections. Set minimum idle
to one and explicitly retain maximum size ten, both overridable using
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE and
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE.

This reduces background pool filling and idle resource use. Hikari normally fills
idle connections asynchronously, so this does not remove nine serial connection
delays from the startup path. The first connection remains required. Under a burst,
the pool may take longer to grow than an already-filled pool; maximum capacity is
unchanged. No specific startup improvement is claimed until measured on Render.

Schema validation remains enabled by default. JWT / BCrypt settings, SQL logging
defaults, connection acquisition timeout, pool fail-fast behavior and eager Spring
initialization are unchanged. No JVM flags, database URL, dependencies, frontend,
or deployment configuration were changed.

## Next Measurements

- Compare several cold starts with the same Render instance and database region.
  Record Hikari start, first connection, Hikari start completion, Hibernate
  EntityManagerFactory completion, and application-ready timestamps.
- Check the effective Render environment uses ddl-auto=validate and SQL logging
  is false. The local .env.example intentionally still uses update and SQL logs.
  Do not apply local schema settings to production.
- Inspect CPU/memory graphs and any GC or out-of-memory events. The current Docker
  image uses a container-aware Java 21 JRE; arbitrary heap caps or compiler flags
  should follow measurements, not guesses.
- Confirm the backend and database are geographically close. Region changes are
  an infrastructure decision and were not made here.
- For a detailed profile, use a temporary Java Flight Recording with Spring's
  FlightRecorderApplicationStartup in a controlled environment. Keep recordings
  private. Do not expose an unauthenticated startup/diagnostics endpoint.
- A paid always-on instance can remove platform sleep and provide more resources;
  it is an optional hosting decision, not part of this change.

Do not replace validate with none/update, enable global lazy initialization, defer
repositories, disable JWT/BCrypt checks, or skip initial database connectivity to
make the ready timestamp look faster. Those changes can hide errors until login.

## Verification

Run from backend/backend:

```powershell
.\mvnw.cmd "-Dtest=ConnectionPoolConfigurationTest,HealthControllerTest" test
.\mvnw.cmd package -DskipTests
```

The configuration tests bind the real application properties without opening a
database connection. The existing full context test requires a reachable PostgreSQL
database with the mapped schema. A real cold-start benchmark still needs Render.

References:

- https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
- https://docs.spring.io/spring-boot/reference/features/spring-application.html
