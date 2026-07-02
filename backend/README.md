# Grocery Household App Backend

A RESTful Spring Boot API for a shared grocery list application. Users can create households, collaborate on grocery lists, invite others to join, and track purchased items.

Built with Spring Boot, JWT authentication, and PostgreSQL.

## Features

### Authentication and security

- User signup and login
- JWT-based authentication
- Stateless backend with no server-side sessions
- Protected endpoints using Spring Security

### Households

- Create households
- View households a user belongs to
- Role-based membership:
  - `OWNER`
  - `MEMBER`
- Only owners can invite new members

### Grocery lists

- Create multiple grocery lists per household
- View all lists within a household
- Share lists among all household members

### List items

- Add items to a grocery list
- Specify quantity, unit, and optional notes
- Mark items as purchased or unpurchased
- Track who purchased an item and when it was purchased
- Soft-delete items using status flags

### Invitations

- Household owners can invite users by email
- Invitations are stored server-side with secure tokens
- Invited users can view pending invitations and accept them
- Invitation tokens are single-use and validated on acceptance

Email delivery is not implemented. Invited users see invitations after signing up and logging in with the invited email.

## Tech stack

- Java 21
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL
- JWT
- Maven

## API endpoints

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/signup` | Create a new user |
| POST | `/api/auth/login` | Login and receive JWT |

### Households

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/households` | List households for current user |
| POST | `/api/households` | Create a household |

### Grocery lists

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/households/{householdId}/lists` | List grocery lists |
| POST | `/api/households/{householdId}/lists` | Create a grocery list |

### List items

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/lists/{listId}/items` | Get items in a list |
| POST | `/api/lists/{listId}/items` | Add an item |
| PATCH | `/api/list-items/{itemId}/purchase` | Mark purchased or unpurchased |
| DELETE | `/api/list-items/{itemId}` | Remove item with soft delete |

### Invitations

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/households/{householdId}/invites` | Create invite, owner only |
| GET | `/api/invites` | View pending invites |
| POST | `/api/invites/{token}/accept` | Accept invite |

## Authentication details

JWTs are returned on signup and login. Protected endpoints require this header:

```text
Authorization: Bearer <JWT>
```

Invitation tokens are separate from JWTs and are used only for accepting household invitations.

## Testing

Endpoints were manually tested using Postman. Tested scenarios include signup and login, JWT authorization enforcement, household creation and access, invitation creation and acceptance, grocery list and item management, and purchased item tracking.

## Running the application

### Prerequisites

- Java 21
- PostgreSQL

### Run

```bash
cd backend/backend
./mvnw spring-boot:run
```
