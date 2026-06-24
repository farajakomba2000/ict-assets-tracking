# ICT Assets Tracking

Spring Boot application for ICT assets tracking with database-backed authentication.

## Features
- PostgreSQL datasource for `ict-assets`
- JWT-based authentication with login and signup flows
- Seeded users: `alice`, `bob`, `admin` (password `password`)
- Basic REST endpoints for assets under `/api/assets`

## Run

1. Ensure PostgreSQL is running and database `ict-assets` exists.
2. Update `src/main/resources/application.yml` if needed.
3. Build and start with Maven:
   ```bash
   mvn spring-boot:run
   ```

## Default seeded users

- `alice` / `password` — `ROLE_USER`
- `bob` / `password` — `ROLE_MANAGER`
- `admin` / `password` — `ROLE_ADMIN`

## API

- `POST /api/auth/login`
- `POST /api/auth/signup`
- `GET /api/assets`
- `POST /api/assets`
