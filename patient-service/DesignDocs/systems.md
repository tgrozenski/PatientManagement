
# System Architecture: Patient Management Service

This document provides a high-level overview of the Patient Management Service's architecture, based on its key dependencies and components.

## Architectural Diagram

The application follows a classic 3-tier architecture:

1.  **Presentation Layer**: Handles HTTP requests and validation.
2.  **Business Logic Layer**: Contains the core application logic.
3.  **Data Access Layer**: Manages data persistence.

```mermaid
graph TD
    subgraph "Client"
        direction LR
        User[User/Client]
    end

    subgraph "Patient Management Service (Spring Boot Application)"
        direction TB

        subgraph "Presentation Layer"
            Controller[PatientController]
        end

        subgraph "Business Logic Layer"
            Service[PatientService]
        end

        subgraph "Data Access Layer"
            Repository[PatientRepository]
        end

        subgraph "Database"
            PostgreSQL
            H2
        end

        Controller -- "Handles HTTP Requests" --> Service
        Service -- "Uses" --> Repository
        Repository -- "Interacts with" --> PostgreSQL
        Repository -- "Interacts with (for tests)" --> H2
    end

    User --> Controller

    style Controller fill:#f9f,stroke:#333,stroke-width:2px
    style Service fill:#ccf,stroke:#333,stroke-width:2px
    style Repository fill:#cfc,stroke:#333,stroke-width:2px
```

### Key Dependencies

-   **Spring Boot Starter Web**: Provides the web layer, including a RESTful API and an embedded Tomcat server.
-   **Spring Boot Starter Data JPA**: Simplifies data access with JPA (Java Persistence API).
-   **PostgreSQL Driver**: Enables communication with a PostgreSQL database.
-   **H2 Database**: An in-memory database, primarily used for testing.
-   **Spring Boot Starter Validation**: Provides data validation capabilities.
