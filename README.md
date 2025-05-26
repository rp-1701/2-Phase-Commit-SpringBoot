# Two-Phase Commit Implementation - Zomato 10-min Delivery

This project demonstrates a simplified implementation of the Two-Phase Commit protocol using a Zomato 10-minute delivery scenario.

## System Components

### 1. Order Service (Coordinator)
- Handles incoming order requests
- Coordinates with Item and Delivery services
- Manages the two-phase commit protocol

### 2. Item Service (Participant 1)
- Manages item inventory
- Checks item availability in nearby stores
- Reserves items during the commit phase

### 3. Delivery Service (Participant 2)
- Manages delivery agents
- Checks agent availability
- Assigns agents to orders during commit phase

## Two-Phase Commit Flow

### Phase 1 (Prepare)
1. Order Service receives delivery request
2. Coordinator asks Item Service to check item availability
3. Coordinator asks Delivery Service to check agent availability
4. Both services respond with READY or ABORT

### Phase 2 (Commit/Rollback)
- If both services respond READY:
  - Coordinator sends COMMIT
  - Item is reserved
  - Delivery agent is assigned
- If any service responds ABORT:
  - Coordinator sends ROLLBACK
  - Resources are released

## Project Structure
```
├── Order-Service/     # Coordinator Service
├── Item-Service/      # Participant 1
└── Delivery-Service/  # Participant 2
```

## Technologies Used
- Spring Boot
- Spring Data JPA
- RESTful APIs
- H2 Database (for simplicity) 