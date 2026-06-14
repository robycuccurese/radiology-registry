# Local Setup Guide (OS-agnostic)

This guide explains how to configure and run the project on Windows, macOS, or Linux.

## Prerequisites

- JDK 21
- Docker with Compose (`docker compose` command)
- Git (recommended)

The project ships with Maven Wrapper, so a global Maven installation is optional.

## 1) Clone the repository

```bash
git clone <repository-url>
cd radiology-registry
```

## 2) Start PostgreSQL

```bash
docker compose -f docker/devops/docker-compose.yml up -d
```

This setup initializes two databases:

- `radiology_registry_dev` (application runtime)
- `radiology_registry_test` (test profile)

If you need a clean database re-initialization:

```bash
docker compose -f docker/devops/docker-compose.yml down -v
docker compose -f docker/devops/docker-compose.yml up -d
```

## 3) Run the application

### Linux/macOS

```bash
chmod +x ./mvnw
./mvnw spring-boot:run
```

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

## 4) Verify startup

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Main endpoint: `GET /api/organizzazioni/{id}/tree`

## 5) Run tests

### Full test suite

Linux/macOS:

```bash
chmod +x ./mvnw
./mvnw test
```

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

### Integration test only (explicit test profile)

Linux/macOS:

```bash
./mvnw "-Dspring.profiles.active=test" "-Dtest=OrganizationTreePersistenceIT" test
```

Windows PowerShell:

```powershell
.\mvnw.cmd "-Dspring.profiles.active=test" "-Dtest=OrganizationTreePersistenceIT" test
```

## Configuration notes

- Default profile (`application.yml`) points to `radiology_registry_dev`.
- Test profile (`application-test.yml`) points to `radiology_registry_test`.
- Test DB variables can be overridden via:
  - `TEST_DB_URL`
  - `TEST_DB_USERNAME`
  - `TEST_DB_PASSWORD`

## Troubleshooting

- `./mvnw: Permission denied`
  - Run `chmod +x ./mvnw`.
- `release version 21 not supported`
  - Ensure JDK 21 is active (`java -version`).
- `docker: command not found`
  - Install/start Docker, then reopen terminal.
- Databases not created as expected
  - Recreate volumes with `docker compose ... down -v`.

