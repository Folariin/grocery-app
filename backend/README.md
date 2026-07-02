# 🛒 Grocery Household App — Backend

A RESTful backend API for a shared grocery list application.  
Users can create households, collaborate on grocery lists, invite others to join, and track purchased items.

Built with **Spring Boot**, **JWT authentication**, and **PostgreSQL**.

---

## ✨ Features

### 🔐 Authentication & Security
- User signup and login
- JWT-based authentication
- Stateless backend (no server-side sessions)
- Protected endpoints using Spring Security

---

### 🏠 Households
- Create households
- View households a user belongs to
- Role-based membership:
    - `OWNER`
    - `MEMBER`
- Only owners can invite new members

---

### 📋 Grocery Lists
- Create multiple grocery lists per household
- View all lists within a household
- Lists are shared among all household members

---

### 🧾 List Items
- Add items to a grocery list
- Specify quantity, unit, and optional notes
- Mark items as purchased or unpurchased
- Track:
    - who purchased an item
    - when it was purchased
- Soft-delete items using status flags

---

### ✉️ Invitations
- Household owners can invite users by email
- Invitations are stored server-side with secure tokens
- Invited users can:
    - view pending invitations
    - accept invitations to join households
- Invitation tokens are single-use and validated on acceptance

> Email delivery is **not implemented**.  
> Invited users see invitations after signing up and logging in with the invited email.

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL
- JWT (JSON Web Tokens)

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|------|--------|------------|
| POST | `/api/auth/signup` | Create a new user |
| POST | `/api/auth/login` | Login and receive JWT |

---

### Households
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/households` | List households for current user |
| POST | `/api/households` | Create a household |

---

### Grocery Lists
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/households/{householdId}/lists` | List grocery lists |
| POST | `/api/households/{householdId}/lists` | Create a grocery list |

---

### List Items
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/lists/{listId}/items` | Get items in a list |
| POST | `/api/lists/{listId}/items` | Add an item |
| PATCH | `/api/list-items/{itemId}/purchase` | Mark purchased / unpurchased |
| DELETE | `/api/list-items/{itemId}` | Remove item (soft delete) |

---

### Invitations
| Method | Endpoint | Description |
|------|--------|------------|
| POST | `/api/households/{householdId}/invites` | Create invite (OWNER only) |
| GET | `/api/invites` | View pending invites |
| POST | `/api/invites/{token}/accept` | Accept invite |

---

## Authentication Details

- JWT is returned on signup and login
- All protected endpoints require the header: # 🛒 Grocery Household App — Backend

A RESTful backend API for a shared grocery list application.  
Users can create households, collaborate on grocery lists, invite others to join, and track purchased items.

Built with **Spring Boot**, **JWT authentication**, and **PostgreSQL**.

---

## Features

### Authentication & Security
- User signup and login
- JWT-based authentication
- Stateless backend (no server-side sessions)
- Protected endpoints using Spring Security

---

### Households
- Create households
- View households a user belongs to
- Role-based membership:
    - `OWNER`
    - `MEMBER`
- Only owners can invite new members

---

### Grocery Lists
- Create multiple grocery lists per household
- View all lists within a household
- Lists are shared among all household members

---

### List Items
- Add items to a grocery list
- Specify quantity, unit, and optional notes
- Mark items as purchased or unpurchased
- Track:
    - who purchased an item
    - when it was purchased
- Soft-delete items using status flags

---

###  Invitations
- Household owners can invite users by email
- Invitations are stored server-side with secure tokens
- Invited users can:
    - view pending invitations
    - accept invitations to join households
- Invitation tokens are single-use and validated on acceptance

> Email delivery is **not implemented**.  
> Invited users see invitations after signing up and logging in with the invited email.

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JPA / Hibernate
- PostgreSQL
- JWT (JSON Web Tokens)

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|------|--------|------------|
| POST | `/api/auth/signup` | Create a new user |
| POST | `/api/auth/login` | Login and receive JWT |

---

### Households
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/households` | List households for current user |
| POST | `/api/households` | Create a household |

---

### Grocery Lists
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/households/{householdId}/lists` | List grocery lists |
| POST | `/api/households/{householdId}/lists` | Create a grocery list |

---

### List Items
| Method | Endpoint | Description |
|------|--------|------------|
| GET | `/api/lists/{listId}/items` | Get items in a list |
| POST | `/api/lists/{listId}/items` | Add an item |
| PATCH | `/api/list-items/{itemId}/purchase` | Mark purchased / unpurchased |
| DELETE | `/api/list-items/{itemId}` | Remove item (soft delete) |

---

### Invitations
| Method | Endpoint | Description |
|------|--------|------------|
| POST | `/api/households/{householdId}/invites` | Create invite (OWNER only) |
| GET | `/api/invites` | View pending invites |
| POST | `/api/invites/{token}/accept` | Accept invite |

---

## Authentication Details

- JWT is returned on signup and login
- All protected endpoints require the header: Authorization: Bearer <JWT>
  Invitation tokens are **separate from JWTs** and are used only for accepting household invitations

---

## Testing

- All endpoints were **manually tested using Postman**
- Tested scenarios include:
- Signup and login
- JWT authorization enforcement
- Household creation and access
- Invitation creation and acceptance
- Grocery list and item management
- Purchased item tracking

---

## Running the Application

### Prerequisites
- Java 17+
- PostgreSQL

### Run
```bash
./mvnw spring-boot:run