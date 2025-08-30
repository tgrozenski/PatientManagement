
# Program Design: Patient Management Service

This document outlines the flow and structure of the Patient Management Service.

## Create Patient Sequence Diagram

The following diagram illustrates the sequence of events when a new patient is created.

```mermaid
sequenceDiagram
    participant Client
    participant PatientController
    participant PatientService
    participant PatientRepository
    participant PatientMapper
    participant Database

    Client->>+PatientController: POST /patients (PatientRequestDTO)
    PatientController->>+PatientService: createPatient(PatientRequestDTO)
    PatientService->>+PatientMapper: toModel(PatientRequestDTO)
    PatientMapper-->>-PatientService: Patient
    PatientService->>+PatientRepository: save(Patient)
    PatientRepository->>+Database: Persist Patient
    Database-->>-PatientRepository: Saved Patient
    PatientRepository-->>-PatientService: Saved Patient
    PatientService->>+PatientMapper: toDTO(Saved Patient)
    PatientMapper-->>-PatientService: PatientResponseDTO
    PatientService-->>-PatientController: PatientResponseDTO
    PatientController-->>-Client: 200 OK (PatientResponseDTO)
```

## Class Diagram

This diagram shows the relationships between the major classes in the service.

```mermaid
classDiagram
    class PatientController {
        -PatientService patientService
        +getPatients() List~PatientResponseDTO~
        +createPatient(PatientRequestDTO) PatientResponseDTO
    }
    class PatientService {
        -PatientRepository patientRepository
        +getPatients() List~PatientResponseDTO~
        +createPatient(PatientRequestDTO) PatientResponseDTO
    }
    class PatientRepository {
        <<interface>>
        +findAll() List~Patient~
        +save(Patient) Patient
    }
    class PatientMapper {
        <<static>>
        +toDTO(Patient) PatientResponseDTO
        +toModel(PatientRequestDTO) Patient
    }
    class Patient {
        -UUID id
        -String name
        -String address
        -String email
        -LocalDate dateOfBirth
        -LocalDate registeredDate
    }
    class PatientRequestDTO {
        -String name
        -String address
        -String email
        -String dateOfBirth
        -String registeredDate
    }
    class PatientResponseDTO {
        -String id
        -String name
        -String address
        -String email
        -String dateOfBirth
    }

    PatientController --> PatientService
    PatientService --> PatientRepository
    PatientService ..> PatientMapper
    PatientRepository --> Patient
    PatientMapper ..> Patient
    PatientMapper ..> PatientRequestDTO
    PatientMapper ..> PatientResponseDTO
```
