# Two-Phase Commit Implementation - Zomato 10-min Delivery

This project demonstrates a simplified implementation of the Two-Phase Commit protocol using a Zomato 10-minute delivery scenario, along with proper concurrency handling using both optimistic and pessimistic locking.

For detailed technical learnings and implementation insights, check out our [LEARNINGS.md](LEARNINGS.md) document.

## Problem Statement
Implement a 10-minute delivery system that ensures:
1. Item availability in nearby store
2. Delivery agent availability
3. Atomic transaction across both resources

## System Components

### 1. Order Service (Coordinator)
- Handles incoming order requests
- Coordinates with Item and Delivery services
- Manages the two-phase commit protocol
- Provides simplified response to end users

### 2. Item Service (Participant 1)
- Manages item inventory
- Checks item availability in nearby stores
- Reserves items during the commit phase
- Handles concurrent access using both optimistic and pessimistic locking

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

## API Endpoints

### Order Service
```
POST /api/orders
- Creates new order
- Coordinates 2PC
- Returns simplified success/failure response
```

### Item Service
```
POST /api/items/prepare
- Checks and reserves item
- Uses pessimistic locking

POST /api/items/commit
- Confirms item reservation

POST /api/items/rollback
- Releases item reservation
```

### Delivery Service
```
POST /api/delivery/prepare
- Checks and reserves delivery agent
- Uses pessimistic locking

POST /api/delivery/commit
- Confirms agent assignment

POST /api/delivery/rollback
- Releases agent reservation
```

## Running the Project

### Prerequisites
- Java 17
- Spring Boot 3.x
- H2 Database (for simplicity)

### Setup
1. Clone the repository
2. Run each service:
```bash
# Start Order Service (port 8080)
./mvnw spring-boot:run -pl Order-Service

# Start Item Service (port 8081)
./mvnw spring-boot:run -pl Item-Service

# Start Delivery Service (port 8082)
./mvnw spring-boot:run -pl Delivery-Service
```