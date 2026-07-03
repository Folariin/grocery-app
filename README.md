# Grocery App

Grocery App is a full-stack household grocery list app. Users can sign up, create shared households, invite members, manage grocery lists together, track purchased items, reset forgotten passwords, and manage basic profile and household settings.

The project is split into a Spring Boot API and a React/Vite frontend.

## Features

- Email/password signup and login with JWT authentication
- Forgot password and reset password flow with hashed reset tokens
- User profile page for viewing email and updating display name
- Household creation and active membership tracking
- Household members view
- Household settings for owner rename, member leave, and sole-owner household close
- Owner-only household invites
- Shared grocery lists per household
- Grocery item creation, purchase toggles, notes, quantities, and soft delete
- Modern responsive frontend built with plain CSS

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- JWT
- Maven

### Frontend

- React
- Vite
- React Router
- Axios
- Plain CSS

## Folder Structure

```text
.
├── README.md
├── backend/
│   ├── README.md
│   └── backend/              # Spring Boot API
│       ├── pom.xml
│       ├── .env.example
│       └── src/
└── grocery-frontend/         # React/Vite frontend
    ├── .env.example
    ├── package.json
    └── src/
```

The backend project intentionally lives at `backend/backend`. Do not flatten this folder structure until the project is ready for a larger repository reorganization.

## Local Setup

### Prerequisites

- Java 21
- Maven or the included Maven wrapper
- Node.js and npm
- PostgreSQL

### Backend

1. Create a local PostgreSQL database, for example `grocerydb`.
2. Copy the backend env example if you want local shell-based configuration:

```bash
cd backend/backend
cp .env.example .env
```

Spring Boot does not automatically load `.env` files by itself. Either export those values in your shell, configure them in your IDE, or update `src/main/resources/application.properties` for local-only development.

For local schema creation, set `SPRING_JPA_HIBERNATE_DDL_AUTO=update`. The committed default is production-safer and uses `validate`.

3. Run the API:

```bash
cd backend/backend
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
cd backend/backend
.\mvnw spring-boot:run
```

By default, the API runs at `http://localhost:8080`.

### Frontend

1. Install dependencies:

```bash
cd grocery-frontend
npm install
```

2. Copy the frontend env example:

```bash
cp .env.example .env
```

3. Start the dev server:

```bash
npm run dev
```

Local development falls back to `http://localhost:8080` if `VITE_API_BASE_URL` is omitted. Production builds require `VITE_API_BASE_URL`.

## Environment Variables

### Backend

| Variable | Purpose | Example |
| --- | --- | --- |
| `APP_ENV` | Runtime environment guard | `local`, `dev`, `production` |
| `APP_CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins allowed by CORS | `https://app.example.com` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://db.example.com:5432/grocerydb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `grocery_app` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | secret value from host |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema mode | `validate` in production, `update` locally |
| `SPRING_JPA_SHOW_SQL` | SQL logging | `false` |
| `SPRING_JPA_FORMAT_SQL` | SQL formatting | `false` |
| `JWT_SECRET` | Secret used to sign JWTs | long random production secret |
| `JWT_EXPIRATION_MS` | JWT lifetime in milliseconds | `86400000` |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Reset token lifetime | `45` |
| `PASSWORD_RESET_FRONTEND_URL` | Reset password page URL | `https://app.example.com/reset-password` |
| `PASSWORD_RESET_DELIVERY_MODE` | Reset link delivery mode | `console` for local/dev only |

### Frontend

| Variable | Purpose | Example |
| --- | --- | --- |
| `VITE_API_BASE_URL` | Backend API base URL, required for production builds | `https://api.example.com` |

## Screenshots

Add screenshots here when the UI is ready to showcase.

Suggested screenshots:

- Login / signup
- Dashboard
- Household page
- Household settings
- Grocery list
- Invites
- Profile

## Deployment Notes

### Backend

- Provision a PostgreSQL database.
- Set all backend environment variables in the hosting platform.
- Set `APP_ENV=production`.
- Set `APP_CORS_ALLOWED_ORIGINS` to the deployed frontend origin.
- Use a strong production `JWT_SECRET`; placeholder/example values are rejected in production.
- Set `PASSWORD_RESET_FRONTEND_URL` to the deployed frontend reset-password route.
- Keep `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` unless you intentionally run a migration/setup step.
- Keep `SPRING_JPA_SHOW_SQL=false` in production.
- Do not use `PASSWORD_RESET_DELIVERY_MODE=console` in production. Console reset-link delivery is blocked when `APP_ENV=production`; add a real email delivery implementation before public launch.
- Build with Maven from `backend/backend`.

### Frontend

- Set `VITE_API_BASE_URL` to the deployed backend URL before building. Production builds fail clearly if it is missing.
- Build from `grocery-frontend`:

```bash
npm run build
```

- Deploy the generated `dist` folder to a static hosting provider.
- Configure the host to serve `index.html` for client-side routes such as `/login`, `/dashboard`, and `/reset-password`.

## Notes

- Real `.env` files are intentionally ignored by Git.
- `.env.example` files are committed as safe templates.
- Local password reset links are logged by the backend console instead of sent by email.
- Production password reset delivery needs a real email implementation before public deployment.
