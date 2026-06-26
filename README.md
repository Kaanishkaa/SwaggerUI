<img width="1512" height="854" alt="Screenshot 2026-06-26 at 3 29 26 PM" src="https://github.com/user-attachments/assets/1649e0d2-61c8-40ef-8bad-5c7b68c9e417" />


<img width="1509" height="660" alt="Screenshot 2026-06-26 at 3 29 37 PM" src="https://github.com/user-attachments/assets/88b41ed6-eb6d-4385-98fd-0bee496f8488" />

[README.md](https://github.com/user-attachments/files/29396031/README.md)
# Employee Directory API — BMAD + Spec Kit Demo

A Spring Boot REST API built entirely through AI agents using the **BMAD Method** inside Claude Code. No human wrote a single line of Java.

---

## What This Is

A demo of Slower LLC's spec-driven AI development workflow. The BMAD Method replaces ad-hoc AI prompting with specialized agents — each owning a distinct phase of the dev lifecycle.

| Agent | What It Did |
|-------|-------------|
| PM Agent | Product brief — goals, requirements, personas, out-of-scope |
| Architect Agent | Tech stack, package structure, data model, API contracts |
| Story Agent | Sized user stories (S/M) with acceptance criteria |
| Dev Agent | Generated all production code files |

---

## Stack

- Java 17 + Spring Boot 3
- PostgreSQL (H2 for local/test)
- Spring Data JPA + Flyway
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito

---

## Run It

```bash
mvn spring-boot:run
```

Then open: `http://localhost:8080/swagger-ui.html`

---

## Project Structure

```
src/
├── main/java/com/company/employeedirectory/
│   ├── EmployeeDirectoryApplication.java
│   ├── config/
│   │   ├── JpaAuditingConfig.java
│   │   └── OpenApiConfig.java
│   ├── domain/
│   │   ├── common/BaseEntity.java
│   │   └── employee/
│   │       ├── Employee.java
│   │       ├── EmployeeRepository.java
│   │       ├── Department.java
│   │       └── EmployeeStatus.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── EmployeeNotFoundException.java
│       └── DuplicateEmailException.java
└── resources/
    ├── application.yml
    └── db/migration/V1__create_employees.sql
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/employees` | List all employees (paginated) |
| GET | `/api/v1/employees/{id}` | Get by ID |
| POST | `/api/v1/employees` | Create employee |
| PUT | `/api/v1/employees/{id}` | Full update |
| PATCH | `/api/v1/employees/{id}` | Partial update |
| DELETE | `/api/v1/employees/{id}` | Soft-delete |
| GET | `/api/v1/employees/search` | Search by name, department, status |
| GET | `/api/v1/departments` | List departments |

---

## BMAD Workflow

```
PM Agent → Architect Agent → Story Agent → Dev Agent
  brief       architecture      stories       code
```

Full Spec Kit integration (`/specify` → `/plan` → `/tasks`) pending GitHub Copilot org license.

---

*Built with [BMAD Method](https://github.com/bmadcode/bmad-method) + Claude Code*
