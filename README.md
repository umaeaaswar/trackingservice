
# **Tracking Service**

The **Tracking Service** is a backend application designed to generate and manage tracking numbers for shipments. It provides REST APIs to handle tracking number generation, retrieval of tracking details, and validation of request parameters. The service is designed to be scalable, efficient, and extensible for logistics and e-commerce platforms.

---

## **Table of Contents**
1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [API Documentation](#api-documentation)
6. [Error Handling](#error-handling)
7. [Project Structure](#project-structure)
8. [Future Enhancements](#future-enhancements)

---

## **Features**
- **Tracking Number Generation**: Generates unique tracking numbers based on origin, destination, weight, customer ID, and customer slug.
- **Tracking Details Retrieval**: Fetches shipment status, estimated delivery, and other details based on a tracking ID.
- **Validation**: Ensures input parameters like weight (positive and numeric) and country codes are valid.
- **Scalable Architecture**: Uses asynchronous processing for better performance under load.
- **Interactive API Documentation**: Built-in Swagger UI for exploring and testing APIs.

---

## **Tech Stack**
- **Framework**: Spring Boot 3.x
- **Database**: H2 (Development), MySQL/PostgreSQL (Production)
- **Validation**: Jakarta Bean Validation
- **API Documentation**: Springdoc OpenAPI
- **Build Tool**: Maven
- **Java Version**: 17

---

## **Installation**

### **Prerequisites**
- JDK 17
- Maven 3.6+
- MySQL or PostgreSQL (for production)
- An IDE (e.g., IntelliJ, Eclipse)

### **Steps**
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/tracking-service.git
   cd tracking-service
   ```
2. Update the `application.yml` for database configurations (if required):
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/tracking_service
       username: your_username
       password: your_password
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access the API documentation:
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - OpenAPI: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## **Configuration**
Default configurations are in `application.yml`:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:tracking_service
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
```
For production, update database URL, username, and password as needed.

---

## **API Documentation**

### **Generate Tracking Number**
- **Endpoint**: `GET /v1/api/next-tracking-number`
- **Description**: Generates a unique tracking number for a shipment.
- **Request Parameters**:
  - `originCountryId` (string, required): Origin country code.
  - `destinationCountryId` (string, required): Destination country code.
  - `weight` (decimal, required): Weight of the shipment (positive).
  - `customerId` (UUID, required): Customer ID.
  - `customerSlug` (string, required): Customer-specific identifier.
- **Response**:
  ```json
  {
    "trackingNumber": "USIN550E840031A",
    "createdAt": "2024-12-04T10:00:00Z",
    "status": "SUCCESS",
    "estimatedDelivery": "2024-12-07",
    "priority": "STANDARD"
  }
  ```

### **Retrieve Tracking Details**
- **Endpoint**: `GET /v1/api/tracking-details`
- **Description**: Fetches tracking details for a given tracking ID.
- **Request Parameters**:
  - `trackingId` (string, required): Unique tracking number.
- **Response**:
  ```json
  {
    "trackingNumber": "USIN550E840031A",
    "createdAt": "2024-12-04T10:00:00Z",
    "status": "IN_TRANSIT",
    "estimatedDelivery": "2024-12-07",
    "priority": "STANDARD"
  }
  ```

---

## **Error Handling**
Global error handling is implemented using a `GlobalExceptionHandler`. Examples:
- **Validation Errors**:
  ```json
  {
    "originCountryId": "Origin country ID must be at most 3 characters long",
    "weight": "Weight must be positive"
  }
  ```
- **Custom Errors**:
  - `TrackingIdNotFoundException`: 404 Not Found.
  - `DuplicateTrackingNumberException`: 409 Conflict.

---

## **Project Structure**
```plaintext
src/main/java
├── config              # Global configurations and exception handling
├── controller          # REST controllers for APIs
├── dto                 # Data Transfer Objects
├── exceptions          # Custom exception classes
├── model               # JPA entity classes
├── payload             # Response and request payloads
├── repository          # Spring Data JPA repositories
├── service             # Business logic implementation
└── utils               # Utility classes for tracking number generation
```

---

## **Future Enhancements**
1. **Security**:
   - JWT-based authentication and authorization.
2. **Caching**:
   - Use Redis for caching frequently accessed data.
3. **Third-Party Integration**:
   - Integrate with FedEx, UPS, or other shipping providers.
4. **Notification Service**:
   - Send SMS or email notifications for shipment updates.

This service provides a robust foundation for tracking and logistics management, designed to meet the growing demands of modern e-commerce and supply chain systems.
