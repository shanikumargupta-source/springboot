# Saga Pattern with Kafka and Spring Boot

This project demonstrates the Saga Design Pattern (Choreography-based) using Spring Boot and Apache Kafka.

## Modules

- `common-dto`: Contains shared DTOs and event classes.
- `order-service`: Manages order creation and orchestrates the saga flow.
- `payment-service`: Handles payment processing and refunds.
- `inventory-service`: Manages product stock and releases.

## Saga Flow (Choreography)

1. `Order Service` creates an order in `ORDER_CREATED` status and emits an `OrderEvent`.
2. `Payment Service` and `Inventory Service` listen to `OrderEvent`.
3. `Payment Service` processes payment and emits `PaymentEvent`.
4. `Inventory Service` reserves stock and emits `InventoryEvent`.
5. `Order Service` listens to `PaymentEvent` and `InventoryEvent`:
   - If both are successful, order status becomes `ORDER_COMPLETED`.
   - If any fails (e.g., `PAYMENT_FAILED` or `STOCK_OUT`), `Order Service` marks the order as `ORDER_CANCELLED` and emits a rollback `OrderEvent`.

### Compensation Logic

- If an `OrderEvent` with status `ORDER_CANCELLED` is received:
  - `Payment Service` checks if a payment was made and performs a refund if necessary.
  - `Inventory Service` checks if stock was reserved and releases it back to the inventory.

## Running the Application

1. **Start Kafka and Zookeeper:**
   ```bash
   cd saga-kafka-demo
   docker compose up -d
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run Services:**
   Run each service in a separate terminal:
   ```bash
   mvn spring-boot:run -pl order-service
   mvn spring-boot:run -pl payment-service
   mvn spring-boot:run -pl inventory-service
   ```

4. **Test Scenarios:**

   - **Success Scenario:**
     ```bash
     curl -X POST http://localhost:8081/order/create \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "productId": 1, "amount": 100.0}'
     ```

   - **Payment Failure (Amount > 1000):**
     ```bash
     curl -X POST http://localhost:8081/order/create \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "productId": 1, "amount": 2000.0}'
     ```

   - **Inventory Failure (Stock Out):**
     ```bash
     curl -X POST http://localhost:8081/order/create \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "productId": 2, "amount": 100.0}'
     ```
