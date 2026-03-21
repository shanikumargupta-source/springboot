# Saga Design Pattern Demo

This project demonstrates the **Saga Design Pattern** using an Orchestration-based approach in a Spring Boot application.

## What is the Saga Design Pattern?
The Saga pattern is a failure management pattern for maintaining data consistency across multiple microservices without using distributed transactions. It breaks a long-running transaction into a sequence of smaller, local transactions.

Each local transaction:
1. Updates its own database.
2. Triggers the next step in the saga.
3. Provides a **compensating transaction** to undo its changes if a subsequent step fails.

## Use of Saga
- **Distributed Systems**: When a single business process spans multiple services (e.g., Order, Payment, Inventory).
- **Scalability**: Unlike 2PC (Two-Phase Commit), Saga doesn't lock resources for the entire duration, allowing for higher throughput.
- **Reliability**: Ensures the system eventually reaches a consistent state, even when some steps fail.

## How it's Implemented Here
- **Orchestrator**: `OrderService` manages the flow of the saga.
- **Participants**: `InventoryService` and `PaymentService`.
- **Flow**:
    1. `OrderService` starts the saga.
    2. `InventoryService` reserves stock.
    3. `PaymentService` processes payment.
    4. If payment fails (e.g., amount > $1000), `InventoryService` is called to release the reserved stock (Compensation).

## Running the Project
1. Run the application:
   ```bash
   mvn spring-boot:run
   ```
2. Place an order (Success):
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"productId": "PRODUCT1", "quantity": 2, "price": 10.0}' http://localhost:8080/api/orders/place
   ```
3. Trigger Inventory Failure (Insufficient Stock):
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"productId": "PRODUCT2", "quantity": 100, "price": 10.0}' http://localhost:8080/api/orders/place
   ```
4. Trigger Payment Failure (Compensation):
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"productId": "PRODUCT1", "quantity": 1, "price": 2000.0}' http://localhost:8080/api/orders/place
   ```
