# Two-Phase Commit Implementation

This project demonstrates a practical implementation of the Two-Phase Commit (2PC) protocol across multiple microservices for handling distributed transactions in a 10 min food delivery system.

## 10-Minute Delivery Requirements

For a successful 10-minute delivery, the system must guarantee two critical conditions simultaneously:
1. **Item Availability**: The ordered item must be available in a nearby store
2. **Delivery Agent Assignment**: A delivery agent must be available and assigned to the order

The Two-Phase Commit protocol ensures these conditions are met atomically:
- Both resources (item and agent) must be successfully reserved
- If either resource is unavailable, the entire order is rolled back
- No partial allocations are allowed to prevent resource deadlocks

## Services Overview

1. **Order Service** (Port: 8080)
   - Coordinates the 2PC process
   - Handles order creation and management
   - Communicates with Item and Delivery services
   - Ensures atomic allocation of both item and delivery agent

2. **Item Service** (Port: 8081)
   - Manages item inventory
   - Handles item reservation and commitment
   - Uses optimistic locking for concurrent access
   - Ensures items are available in nearby stores

3. **Delivery Service** (Port: 8082)
   - Manages delivery agent allocation
   - Uses FOR UPDATE SKIP LOCKED for concurrent agent selection
   - Handles agent reservation and commitment
   - Assigns nearest available delivery agent

## Setup and Running

1. Start each service in separate terminals:
   ```bash
   # Terminal 1 - Order Service
   cd Order-Service
   ./mvnw spring-boot:run

   # Terminal 2 - Item Service
   cd Item-Service
   ./mvnw spring-boot:run

   # Terminal 3 - Delivery Service
   cd Delivery-Service
   ./mvnw spring-boot:run
   ```

2. The simulation will automatically run when you start the Order Service with the 'test' profile.

## Testing Concurrent Orders

The system includes a simulation tool that tests concurrent order processing:

- Simulates 15 concurrent orders
- Tests resource allocation under high concurrency
- Provides detailed logging of the 2PC process
- Shows success/failure statistics

## Key Features

- Distributed transaction management using 2PC
- Concurrent resource allocation handling
- Optimistic locking for inventory management
- Skip-locked row selection for delivery agents
- Comprehensive logging and monitoring
- Automatic rollback on failures
- Atomic allocation of items and delivery agents
- Guaranteed resource availability before order confirmation

## H2 Console Access

Each service has its own H2 database console accessible at:
- Order Service: http://localhost:8080/h2-console
- Item Service: http://localhost:8081/h2-console
- Delivery Service: http://localhost:8082/h2-console