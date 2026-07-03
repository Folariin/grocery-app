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

Key settings:

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION_MS` | JWT lifetime in milliseconds |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Password reset token lifetime |
| `PASSWORD_RESET_FRONTEND_URL` | Frontend reset-password URL |

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
| POST | `/api/auth/forgot-password` | Request a password reset link |
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
- Password reset tokens are generated securely and stored hashed.
- Forgot-password responses are intentionally generic to avoid account enumeration.
- Household actions enforce active membership and owner checks on the backend.

## Deployment Notes

- Set all environment variables in the deployment platform.
- Use a production PostgreSQL database.
- Use a strong production `JWT_SECRET`.
- Set `PASSWORD_RESET_FRONTEND_URL` to the deployed frontend reset-password page.
- Review `spring.jpa.hibernate.ddl-auto` before production. Prefer migrations for long-term production use.
- Build from `backend/backend` with Maven.

## Testing

There is no formal test suite documented yet. Before deployment, manually verify:

- Signup and login
- Forgot password and reset password
- Profile display-name update
- Household create, rename, leave, and sole-owner close
- Invite creation and acceptance
- Grocery list and item workflows
