# Grocery App

This repository contains a Spring Boot backend and a React/Vite frontend for a shared grocery household application.

## Repository layout

- `backend/backend` - Spring Boot API
- `grocery-frontend` - React/Vite frontend

The backend project is intentionally nested under `backend/backend`. Do not flatten this structure until the project is ready for a larger repository reorganization.

## Backend

The backend API lives in `backend/backend`.

Typical local workflow:

```bash
cd backend/backend
./mvnw spring-boot:run
```

The API is configured for local PostgreSQL development. Update local environment-specific settings before running against your own database.

## Frontend

The frontend app lives in `grocery-frontend`.

Typical local workflow:

```bash
cd grocery-frontend
npm install
npm run dev
```

The frontend reads the API base URL from `VITE_API_BASE_URL`. If it is not set, it defaults to `http://localhost:8080`.

Example local override:

```bash
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```
