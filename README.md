# Radiology Registry (POC)

Java/Spring Boot backend for hierarchical management of:

- Organization (root)
- Nested Containers (theoretically unlimited depth)
- Radiology Equipment

## Requirements

- JDK 21
- Docker (optional but recommended)
- Maven Wrapper included (`mvnw` / `mvnw.cmd`)

## Quick Start (Local Setup)

1) Start PostgreSQL with Docker Compose:

```powershell
Set-Location "C:\Workspaces\Personal\radiology-registry"
docker compose -f .\docker\devops\docker-compose.yml up -d
```

Database naming in this setup:

- `radiology_registry_dev` for local development/runtime
- `radiology_registry_test` for test profile execution

If your PostgreSQL volume already existed before this change, run a fresh init so the
`radiology_registry_test` database is created from `docker/devops/init/01-create-test-db.sql`.

2) Start the application:

```powershell
Set-Location "C:\Workspaces\Personal\radiology-registry"
.\mvnw.cmd spring-boot:run
```

3) Main endpoint:

- `GET /api/organizzazioni/{id}/tree` — Retrieves complete organization tree

Swagger UI (when running locally): `http://localhost:8080/swagger-ui.html`

## Architectural Choices (Brief Overview)

- **Stack**: Spring Boot + Spring Data JPA + PostgreSQL + Flyway.
- **Hierarchical model**: `node` table with recursive `parent_id` relation and JOINED inheritance for `organization`, `container`, `equipment`.
- **Recursion strategy**:
  - Primary path: filter by `root_org_id` (highly efficient for full tree reads);
  - Safety fallback: `WITH RECURSIVE` query for compatibility with legacy inconsistent data.
- **root_org_id valorization**:
  - Copy from parent (no tree traversal, avoids N+1)
  - DB trigger (V9) enforces automatic valorization and prevents inconsistencies
  - Stateless validation at service level (parent.getRootOrganizationId())
- **GET tree performance**:
  - Single flat query to fetch all organization nodes;
  - O(n) in-memory reconstruction via id → dto map;
  - Dedicated DB index on (root_org_id, parent_id).

## Endpoint Details: `GET /api/organizzazioni/{id}/tree`

The endpoint returns the complete hierarchy:

- Organization
- N-level nested Containers
- Equipment under Containers or directly under Organization

Implementation:

1) Validate organization existence,
2) Retrieve full tree via `root_org_id`,
3) Apply recursive fallback if necessary,
4) Map to `NodeTreeDto` with equipment details (serial number + installation date).

## API Reference

### GET /api/organizzazioni/{id}/tree
Retrieves complete organization tree.

**Response (200):**
```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "name": "ASL 1",
  "type": "ORGANIZATION",
  "children": [
    {
      "id": "22222222-2222-2222-2222-222222222222",
      "name": "Building A",
      "type": "CONTAINER",
      "children": [
        {
          "id": "33333333-3333-3333-3333-333333333333",
          "name": "GE Revolution CT",
          "type": "EQUIPMENT",
          "equipmentDetails": {
            "serialNumber": "GE-REV-001",
            "installationDate": "2024-01-15"
          },
          "children": []
        }
      ],
      "equipmentDetails": null
    }
  ],
  "equipmentDetails": null
}
```

## Testing

Run tests:

```powershell
Set-Location "C:\Workspaces\Personal\radiology-registry"
.\mvnw.cmd test
```

Run integration tests with explicit test profile:

```powershell
Set-Location "C:\Workspaces\Personal\radiology-registry"
.\mvnw.cmd "-Dspring.profiles.active=test" "-Dtest=OrganizationTreePersistenceIT" test
```

Test strategy implemented for the POC:

- **Unit tests (business logic)**
  - `OrganizationServiceTest` — tree reconstruction and legacy fallback behavior
  - `EquipmentServiceTest` — equipment creation rules and root organization propagation
  - `HierarchyValidatorTest` — hierarchy constraints (allowed/forbidden parent-child combinations)
- **Security tests**
  - `EquipmentControllerSecurityTest` — POST protection with `X-User-Role: ADMIN`
  - `RoleHeaderAuthenticationFilterTest` — header-to-role mapping in security context
- **Integration test (real database persistence/retrieval)**
  - `OrganizationTreePersistenceIT` — persists Organization/Container/Equipment and reads back the full tree from PostgreSQL through repository and service

### Test Classification (Unit vs Slice vs Integration)

| Test class | Classification | Why |
|---|---|---|
| `OrganizationServiceTest` | Unit | Pure service logic with mocked repositories (`Mockito`), no Spring context startup. |
| `EquipmentServiceTest` | Unit | Pure service logic with mocked collaborators (`NodeRepository`, `HierarchyValidator`). |
| `HierarchyValidatorTest` | Unit | Domain rule validation in isolation, no application context. |
| `RoleHeaderAuthenticationFilterTest` | Unit | Filter behavior tested with mock servlet objects and direct invocation. |
| `EquipmentControllerSecurityTest` | Slice (Web MVC) | Uses `@WebMvcTest(EquipmentController.class)` and loads only the web/security slice. |
| `OrganizationTreePersistenceIT` | Integration | Uses `@SpringBootTest` with real persistence flow against test PostgreSQL DB. |
| `RadiologyRegistryApplicationTests` | Integration (context load) | Uses `@SpringBootTest` to verify full Spring context startup. |

Configuration notes:

- Default app datasource (`application.yml`) targets `radiology_registry_dev`.
- Test profile datasource (`application-test.yml`) targets `radiology_registry_test`.
- Integration tests activate `test` profile to keep development and test data isolated.

## CI Pipeline

The repository includes three GitHub Actions workflows:

- `.github/workflows/ci.yml` (build + test)
- `.github/workflows/release.yml` (image build/scan/publish)
- `.github/workflows/deploy.yml` (environment deployment)

### `ci.yml` - Build and Test

- Trigger: push and pull request on `develop`, plus manual run
- Runs tests with PostgreSQL service and `SPRING_PROFILES_ACTIVE=test`
- Publishes per-test-class outcome summary (PASSED/FAILED)
- Uploads CI artifacts: JUnit/Surefire reports, Maven test log, packaged jar, build metadata, brief changelog
- Enables concurrency cancellation on same ref and enforces timeout
- Adds observability summary with commit, run link, version, and brief changelog

### `release.yml` - Publish Docker Image

- Trigger: automatic after successful CI on `develop` (`workflow_run`) or manual run
- Builds Docker image and publishes to GHCR with semantic and immutable tags
  - `branchName-version` (example `develop-0.0.1`)
  - `branchName-version-shortSha` (example `develop-0.0.1-a1b2c3d`)
- Runs Trivy scans (filesystem + image) and uploads SARIF to GitHub Security
- Uploads release metadata/changelog and publishes image digest in workflow summary
- Enables concurrency cancellation and timeout

### `deploy.yml` - Deploy per Environment

- Trigger: automatic after successful release (`workflow_run`) for default `dev`, or manual dispatch for `dev`/`staging`/`prod`
- Resolves target image from GHCR, validates image availability, resolves digest for traceability
- Supports optional deployment webhook via `DEPLOY_WEBHOOK_URL` secret
- Uploads deployment metadata/changelog artifacts and publishes observability summary
- Uses environment-specific concurrency and timeout
