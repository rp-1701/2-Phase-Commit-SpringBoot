# Two-Phase Commit Implementation Learnings

## System Overview
- Implemented 2PC for Zomato 10-min delivery system
- Three microservices: Order (Coordinator), Item (Participant 1), Delivery (Participant 2)
- Goal: Ensure atomic operations across services for order placement

## Two-Phase Commit Flow
1. **Prepare Phase**
   - Order service initiates the transaction
   - Asks Item service to check and reserve item
   - Asks Delivery service to check and reserve agent
   - Both participants must respond READY or ABORT

2. **Commit/Rollback Phase**
   - If both READY: Coordinator sends COMMIT
   - If any ABORT: Coordinator sends ROLLBACK
   - Resources are either committed or released

## Locking Mechanisms

### Pessimistic Locking
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Item> findByItemId(Long itemId);
```
- Acquires actual database locks
- Prevents concurrent access
- Used for critical operations
- Row-level locking, not table-level
- Best for high-contention scenarios

### Optimistic Locking
```java
@Version
private Long version;
```
- Automatically enabled by @Version annotation
- No actual database locks
- Uses version checking
- Best for low-contention scenarios
- Handles concurrent modifications through version comparison

### Key Differences
- Pessimistic: Locks immediately, prevents access
- Optimistic: Checks at commit time, allows concurrent access
- Pessimistic: Higher guarantee, lower concurrency
- Optimistic: Lower guarantee, higher concurrency

## Important Concepts

### Lost Update Problem
- Occurs when two transactions read and update same data
- Later update overwrites earlier update without considering changes
- Solved by either pessimistic or optimistic locking

### Row-Level vs Table-Level Locking
- Row-level: Locks specific rows only
- Better concurrency and performance
- Allows other transactions to access different rows
- Used in our implementation for both Item and Delivery services

### Version Field Behavior
- Automatically increments on save
- Works with both locking mechanisms
- With pessimistic lock: Version check always passes
- Without pessimistic lock: Version check prevents concurrent modifications

## Best Practices

### When to Use Pessimistic Locking
1. Critical operations requiring immediate consistency
2. High-contention scenarios
3. Short-duration transactions
4. When retry is expensive

### When to Use Optimistic Locking
1. Read-heavy operations
2. Low-contention scenarios
3. Long-running transactions
4. When occasional retries are acceptable

### Using Both Mechanisms
- Provides defense in depth
- Allows flexibility in access patterns
- Pessimistic for critical operations
- Optimistic as a safety net

## Implementation Details

### Order Service (Coordinator)
- Manages the 2PC protocol
- Coordinates with participants
- Handles success/failure responses
- Provides simple response to end user

### Item Service (Participant 1)
- Manages inventory
- Handles item reservation
- Uses both locking mechanisms
- Prevents overselling

### Delivery Service (Participant 2)
- Manages delivery agents
- Handles agent reservation
- Uses location-based assignment
- Prevents double booking

## Error Handling
- Pessimistic: Prevents conflicts upfront
- Optimistic: Handles conflicts through exceptions
- Both: Provide transaction rollback capability
- Proper error responses to coordinator

## Performance Considerations
1. Use row-level locking for better concurrency
2. Keep transactions short
3. Choose appropriate locking mechanism
4. Handle timeouts properly
5. Consider retry strategies

## Future Improvements
1. Add timeout mechanisms
2. Implement retry strategies
3. Add monitoring and logging
4. Consider distributed transaction patterns
5. Implement recovery mechanisms

## Testing Scenarios
1. Concurrent order placement
2. Resource contention
3. Participant failures
4. Network issues
5. Timeout scenarios 