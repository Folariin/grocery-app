# Grocery Household App Backend

This folder documents the Spring Boot API for the Grocery App project. The actual backend project lives in `backend/backend`.

The API handles authentication, users, households, memberships, invites, grocery lists, grocery items, profile updates, household settings, and forgot-password/reset-password flows.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- JWT
- Maven
- Configurable password reset delivery: console, disabled, or Resend

## Project Location

```text
backend/
├── README.md
└── backend/
    ├── pom.xml
    ├── .env.example
    └── src/
```

The nested `backend/backend` path is intentional for now.

## Local Setup

### Prerequisites

- Java 21
- PostgreSQL
- Maven or the included Maven wrapper

### Configuration

Example environment values are documented in `backend/backend/.env.example`.

Spring Boot does not automatically load `.env` files on its own. For local development, either export the variables in your shell, configure them in your IDE, or use `backend/backend/src/main/resources/application.properties` for local-only settings.

The committed defaults are production-safer: SQL logging is off and Hibernate uses `validate`. For local database bootstrapping, set `SPRING_JPA_HIBERNATE_DDL_AUTO=update` intentionally.

Key settings:

| Variable | Purpose |
| --- | --- |
| `APP_ENV` | Runtime environment guard, such as `local` or `production` |
| `APP_CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins allowed by CORS |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema mode |
| `SPRING_JPA_SHOW_SQL` | SQL logging flag |
| `SPRING_JPA_FORMAT_SQL` | SQL formatting flag |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION_MS` | JWT lifetime in milliseconds |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Password reset token lifetime |
| `PASSWORD_RESET_FRONTEND_URL` | Frontend reset-password URL |
| `PASSWORD_RESET_DELIVERY_MODE` | Reset link delivery mode: `console`, `disabled`, or `resend` |
| `RESEND_API_KEY` | Resend API key, required when delivery mode is `resend` |
| `RESEND_FROM_EMAIL` | Verified Resend sender email/domain |
| `RESEND_FROM_NAME` | Display name for reset emails |

### Run

```bash
cd backend/backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend/backend
.\mvnw spring-boot:run
```

The API runs at `http://localhost:8080` by default.

## API Overview

Protected endpoints require:

```text
Authorization: Bearer <JWT>
```

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/signup` | Create a user and return a JWT |
| POST | `/api/auth/login` | Login and return a JWT |
| POST | `/api/auth/forgot-password` | Request a password reset link, or return a safe unavailable message when reset delivery is disabled |
| POST | `/api/auth/reset-password` | Reset password with a valid token |

For local development, reset links are logged to the backend console instead of sent by email.

### User Profile

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/users/me` | View current user's display name and email |
| PATCH | `/api/users/me` | Update current user's display name |

### Households

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/households` | List current user's active households |
| POST | `/api/households` | Create a household |
| GET | `/api/households/{householdId}` | View household details for an active member |
| PATCH | `/api/households/{householdId}` | Rename household, owner only |
| POST | `/api/households/{householdId}/leave` | Leave household, non-owner members only |
| DELETE | `/api/households/{householdId}` | Close household, sole owner only |
| GET | `/api/households/{householdId}/members` | List active household members |

### Invitations

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/households/{householdId}/invites` | Create invite, owner only |
| GET | `/api/invites` | View pending invites for current user |
| POST | `/api/invites/{token}/accept` | Accept an invite |

Invites are visible to users who sign up and log in with the invited email address.

### Grocery Lists

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/households/{householdId}/lists` | List grocery lists in a household |
| POST | `/api/households/{householdId}/lists` | Create a grocery list |

### List Items

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/lists/{listId}/items` | Get active items in a list |
| POST | `/api/lists/{listId}/items` | Add an item |
| PATCH | `/api/list-items/{itemId}/purchase` | Mark an item purchased or needed |
| DELETE | `/api/list-items/{itemId}` | Soft-delete an item |

## Security Notes

- JWTs are stateless and stored by the frontend.
- Passwords are hashed using the existing Spring Security password encoder.
- Password reset tokens are generated securely and stored hashed when delivery is enabled.
- Forgot-password responses avoid account enumeration. Disabled delivery returns a generic unavailable message without checking account existence.
- Household actions enforce active membership and owner checks on the backend.
- Placeholder JWT secrets are rejected when `APP_ENV=production`.
- Console password reset delivery is blocked when `APP_ENV=production`.
- Resend API keys must be stored only in deployment environment variables.

## Password Reset Delivery

`PasswordResetService` delegates reset-link delivery to `PasswordResetDeliveryService` when delivery is enabled.

Current modes:

- `PASSWORD_RESET_DELIVERY_MODE=console` selects `ConsolePasswordResetDeliveryService` for local/dev reset-link logging.
- `PASSWORD_RESET_DELIVERY_MODE=disabled` starts normally without a delivery service. Forgot-password requests return a safe unavailable message and do not generate or store reset tokens.
- `PASSWORD_RESET_DELIVERY_MODE=resend` selects `ResendPasswordResetDeliveryService` for email delivery.
- Console delivery is for local/dev only and is blocked in production.
- Resend delivery requires `RESEND_API_KEY` and `RESEND_FROM_EMAIL`.
- `RESEND_FROM_EMAIL` must use a sender address/domain verified in Resend.
- No Resend secrets are committed to the repository.

## Deployment Notes

- Set all environment variables in the deployment platform.
- Set `APP_ENV=production`.
- Set `APP_CORS_ALLOWED_ORIGINS` to the deployed frontend origin.
- Use a production PostgreSQL database.
- Use a strong production `JWT_SECRET`.
- Set `PASSWORD_RESET_FRONTEND_URL` to the deployed frontend reset-password page.
- Set `PASSWORD_RESET_DELIVERY_MODE=disabled` if production reset email is not configured yet.
- Use `PASSWORD_RESET_DELIVERY_MODE=resend` only after Resend is configured with a verified sender/domain.
- When using Resend, set `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, and optionally `RESEND_FROM_NAME`.
- Keep `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` unless a migration/setup process intentionally uses another value.
- Keep `SPRING_JPA_SHOW_SQL=false` in production.
- Build from `backend/backend` with Maven.

## Testing

There is no formal test suite documented yet. Before deployment, manually verify:

- Signup and login
- Forgot password with delivery mode `console`, `disabled`, and `resend` as appropriate for the environment
- Reset password with a valid token generated while delivery is enabled
- Profile display-name update
- Household create, rename, leave, and sole-owner close
- Invite creation and acceptance
- Grocery list and item workflows
- Resend password reset email delivery in the production/staging environment before enabling `resend`
