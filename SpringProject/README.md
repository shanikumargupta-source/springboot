# Spring Boot CRUD Project

This is a demo project for implementing a RESTful CRUD API with Spring Boot.

## Technologies Used
- **Java 21**
- **Maven 3.9.x**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **H2 In-Memory Database**
- **Lombok**
- **JUnit 5 & Mockito** (for testing)

## How to Run the Application
1. Navigate to the `SpringProject` directory.
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

## How to Run Tests
To run all tests (unit and integration), use the following command from the `SpringProject` directory:
```bash
mvn test
```

## Testing with Postman
You can find a Postman collection in the root folder: `Product_CRUD.postman_collection.json`.

### Steps to Import:
1. Open Postman.
2. Click **Import** in the top left corner.
3. Select the file `Product_CRUD.postman_collection.json` from the `SpringProject` directory.
4. Once imported, you will see the **Spring Boot Product CRUD** collection in your sidebar.

### Testing Endpoints:
- **Base URL**: `http://localhost:8080/api/products`
- **GET All Products**: Returns a list of all products.
- **GET Product by ID**: Returns a single product (e.g., `/api/products/1`).
- **POST Create Product**: Use a JSON body to create a new product.
- **PUT Update Product**: Use a JSON body to update an existing product.
- **DELETE Product**: Deletes a product by ID.

### Sample JSON for POST/PUT:
```json
{
    "name": "Sample Product",
    "price": 19.99,
    "quantity": 50
}
```

## API Features
- **Global Error Handling**: Custom `ProductNotFoundException` returns a `404 Not Found` with a message.
- **Standard HTTP Responses**: Successful deletions return `204 No Content`.
- **In-Memory Database**: All data is stored in memory and resets on application restart.
