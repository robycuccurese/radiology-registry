# Architecture Note

## Context and Goals

The application manages a hierarchical registry for radiology organizations and assets, with the following goals:

- predictable data consistency
- fast full-tree retrieval
- clear domain boundaries
- reproducible delivery across environments

## Main Technology Choices and Rationale

## Language and Framework

- **Java 21 + Spring Boot**
  - Stable ecosystem for backend services and enterprise-grade maintainability.
  - Strong testing support and dependency injection reduce accidental coupling.
  - Good fit for layered architecture (API, application, domain, infrastructure).

## Persistence and Database

- **PostgreSQL**
  - Reliable relational guarantees and strong SQL support for hierarchical queries.
- **Spring Data JPA**
  - Keeps repository code concise while preserving explicit domain modeling.
- **Flyway**
  - Versioned, deterministic schema evolution across local/CI/CD environments.

## Hierarchy Modeling Strategy

The hierarchy is represented by a base `node` concept with parent-child linkage and specialized node types (`organization`, `container`, `equipment`).

Rationale:

- supports deep nesting with a single modeling pattern
- keeps referential integrity in the database
- enables polymorphic behavior while preserving explicit type constraints

## Recursion and Tree Read Strategy

Two complementary strategies are used for tree retrieval:

1. **Primary path**: read by `root_org_id` (indexed path)
2. **Fallback path**: recursive SQL (`WITH RECURSIVE`) for legacy/inconsistent data

Rationale:

- primary path optimizes common reads and scales better for full-tree requests
- fallback protects compatibility and operational resilience

## `root_org_id` Propagation and Consistency

`root_org_id` is propagated from the parent at write time and enforced in the database.

Rationale:

- avoids repeated tree traversals (prevents N+1 behavior)
- enables efficient filtering for full hierarchy reads
- keeps application and database aligned on consistency rules

## API Mapping Performance

Tree reconstruction is done in memory using an id-to-dto map after a flat query.

Rationale:

- O(n) reconstruction
- simple and deterministic mapping logic
- minimal query chatter

## Database Schema Created by SQL Migrations

The schema is created by Flyway migrations in `src/main/resources/db/migration`.

### Core tables

1. `node`
   - Columns: `id` (PK), `name`, `node_type`, `parent_id` (FK), audit columns, `root_org_id` (FK)
   - Purpose: base table for the whole hierarchy

2. `organization`
   - Columns: `id` (PK, FK -> `node.id`)
   - Purpose: typed extension for root organizational nodes

3. `container`
   - Columns: `id` (PK, FK -> `node.id`)
   - Purpose: typed extension for nested container nodes

4. `equipment`
   - Columns: `id` (PK, FK -> `node.id`), `equipment_type`, `serial_number`, `installation_date`
   - Purpose: typed extension for equipment-specific attributes

### Relationships

- Self-reference hierarchy:
  - `node.parent_id -> node.id` (`fk_node_parent`)
  - Represents parent-child relationships for arbitrary depth

- Root organization linkage:
  - `node.root_org_id -> node.id` (`fk_node_root_org`)
  - Points every node to the root organization node of its tree

- JOINED inheritance pattern:
  - `organization.id -> node.id` (`fk_org_node`, `ON DELETE CASCADE`)
  - `container.id -> node.id` (`fk_container_node`, `ON DELETE CASCADE`)
  - `equipment.id -> node.id` (`fk_equipment_node`, `ON DELETE CASCADE`)

### Indexes

- `idx_node_parent` on `node(parent_id)`
- `idx_node_root_org_id` on `node(root_org_id)`
- `idx_node_root_parent` on `node(root_org_id, parent_id)`

These indexes support fast tree traversal and full-tree reads by organization root.

### Triggers and data integrity rules

From `V9__add_root_org_id_triggers.sql`:

- `tg_enforce_root_org_id` (`BEFORE INSERT` on `node`)
  - If `parent_id IS NULL`: sets `root_org_id = id`
  - Else: copies `root_org_id` from parent

- `tg_prevent_root_org_id_update` (`BEFORE UPDATE` on `node`)
  - Prevents manual updates to `root_org_id`

Rationale:

- keeps hierarchy-consistent root linkage in the database layer
- guarantees stable query semantics for root-filtered tree reads
- prevents accidental corruption from out-of-band writes

## DevOps and Delivery Strategy

The CI/CD design separates concerns into three workflows:

- `ci.yml`: build + tests + artifacts + observability summary
- `release.yml`: image build, security scanning, GHCR publish, digest traceability
- `deploy.yml`: environment promotion with controlled concurrency and deployment metadata

Rationale:

- cleaner pipeline responsibilities
- safer promotion model
- easier troubleshooting with dedicated artifacts and summaries

## Testing Strategy

The test suite is intentionally layered:

- **Unit tests** for business rules and service behavior
- **Slice tests** for web/security boundaries
- **Integration tests** for real persistence and retrieval against PostgreSQL

Rationale:

- fast feedback for logic regressions
- realistic verification at boundaries
- confidence on end-to-end data behavior

## Demo Data Seed (`V5__seed_demo_hierarchy`)

After `V5__seed_demo_hierarchy.sql`, the database contains a ready-to-use demo tree rooted at one organization.

### Seeded hierarchy

```text
ASL 1 (ORGANIZATION) [11111111-1111-1111-1111-111111111111]
|- Mobile CT Scanner (EQUIPMENT) [66666666-6666-6666-6666-666666666666]
`- Building A (CONTAINER) [22222222-2222-2222-2222-222222222222]
   `- Building 2 (CONTAINER) [33333333-3333-3333-3333-333333333333]
      `- Radiology Department (CONTAINER) [44444444-4444-4444-4444-444444444444]
         `- GE Revolution CT (EQUIPMENT) [55555555-5555-5555-5555-555555555555]
```

### Records inserted by table

- `node`: 6 rows
  - 1 organization node
  - 3 container nodes
  - 2 equipment nodes
- `organization`: 1 row (`ASL 1`)
- `container`: 3 rows (`Building A`, `Building 2`, `Radiology Department`)
- `equipment`: 2 rows
  - `GE Revolution CT` (`CT`, serial `GE-REV-001`, installation date `2024-01-15`)
  - `Mobile CT Scanner` (`CT`, serial `MOBILE-CT-001`, installation date `2024-03-20`)

This seed is useful for quick local validation of tree reads (`GET /api/organizzazioni/{id}/tree`) and for manual API exploration in Swagger.

