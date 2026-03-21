# Microservices Design Patterns Demo

This project demonstrates several key microservice design patterns using Java and Spring Boot.

## Patterns Implemented

### 1. API Gateway Pattern
- **Module:** `api-gateway`
- **Implementation:** Uses **Spring Cloud Gateway**. It acts as a single entry point for all client requests, routing them to the appropriate backend services (`order-service`, `payment-service`, `inventory-service`).
- **Configuration:** See `api-gateway/src/main/resources/application.yml`.

### 2. Circuit Breaker Pattern
- **Module:** `api-gateway`
- **Implementation:** Uses **Resilience4j** integrated with Spring Cloud Gateway.
- **Use Case:** When the `order-service` is down or slow, the gateway trips the circuit and returns a fallback response defined in `FallbackController.java`. This prevents cascading failures.

### 3. Saga Pattern (Choreography/Orchestration Simplified)
- **Module:** `order-service`
- **Implementation:** The `OrderService.createOrder` method coordinates a distributed transaction.
- **Workflow:**
    1. Create order in `CREATED` state.
    2. Call `inventory-service` to check stock.
    3. Call `payment-service` to process payment.
    4. If any step fails, it executes a **Compensating Transaction** (`compensate` method) to set the order status to `CANCELLED`.
    5. If all steps succeed, the order status is set to `COMPLETED`.

### 4. Database per Service Pattern
- **Module:** `order-service`
- **Implementation:** The `order-service` has its own H2 in-memory database configuration, independent of other services. This ensures data encapsulation and loose coupling.

### 5. Externalized Configuration
- **Implementation:** Each service uses its own `application.yml` for configuration, which can be further externalized using Spring Cloud Config in a production environment.

## Project Structure
- `api-gateway`: Port 8080
- `order-service`: Port 8081
- `payment-service`: Port 8082
- `inventory-service`: Port 8083

## How to Run
From the `microservices-demo` directory, you can build all modules:
```bash
mvn clean install
```
Then run each service independently (in a real environment).
