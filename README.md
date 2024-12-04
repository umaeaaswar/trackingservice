
# **Tracking Service**

The **Tracking Service** is a backend application designed to generate and manage tracking numbers for shipments. It provides REST APIs to handle tracking number generation, retrieval of tracking details, and validation of request parameters. The service is built for logistics and e-commerce platforms, ensuring scalability, efficiency, and extensibility.

---
## **Application Access**
This application is deployed and accessible on **AWS Elastic Beanstalk**:  
[http://tracking-service.ap-south-1.elasticbeanstalk.com/swagger-ui/index.html](http://tracking-service.ap-south-1.elasticbeanstalk.com/swagger-ui/index.html)

Use the above URL to explore the Swagger UI and interact with the APIs.

---

## **Table of Contents**
1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [API Documentation](#api-documentation)
6. [Error Handling](#error-handling)
7. [Profiles and Environments](#profiles-and-environments)
8. [Database Setup](#database-setup)
9. [Cloud Deployment and Scalability](#cloud-deployment-and-scalability)
10. [Project Structure](#project-structure)
11. [Testing](#testing)
12. [Future Enhancements](#future-enhancements)

---

## **Features**
- **Tracking Number Generation**: Generates unique tracking numbers based on various parameters (origin, destination, weight, etc.).
- **Tracking Details Retrieval**: Fetches shipment details like status, estimated delivery, and priority.
- **Validation**: Ensures inputs are valid with constraints such as positive weights and valid country codes.
- **Interactive API Documentation**: Uses Swagger/OpenAPI for easy testing and exploration of APIs.
- **Scalable Design**: Supports high concurrency with asynchronous processing.

---

## **Tech Stack**
- **Framework**: Spring Boot 3.x
- **Database**:
    - H2 (for Development and Testing)
    - MySQL (for Production)
- **Validation**: Jakarta Bean Validation
- **API Documentation**: Springdoc OpenAPI
- **Build Tool**: Maven
- **Java Version**: 17
- **Deployment**: AWS Elastic Beanstalk with MySQL RDS, Docker-ready architecture

---

## **Installation**

### **Prerequisites**
- JDK 17 or later
- Maven 3.6 or later
- MySQL database (for production)
- An IDE like IntelliJ IDEA or Eclipse

### **Steps**
1. **Clone the Repository**
   ```bash
   git clone https://github.com/iambharath-ashok/tracking-service.git
   cd tracking-service
   ```

2. **Set Up the Environment**
   Update environment variables for production:
   ```bash
   export SPRING_DATASOURCE_USERNAME=<your-database-username>
   export SPRING_DATASOURCE_PASSWORD=<your-database-password>
   ```

3. **Build the Project**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   Alternatively, use the JAR file:
   ```bash
   java -jar target/tracking-service-0.0.1-SNAPSHOT.jar
   ```

5. **Access the Application**
    - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
    - OpenAPI Docs: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## **Configuration**

### **Default Configuration**
The default configuration (`application.properties`) is optimized for local development:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### **Production Configuration**
For production, use the `application-prod.properties` file:
```properties
spring.datasource.url=jdbc:mysql://<host>:3306/tracking-service
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

---

## **API Documentation**

### **Swagger UI**
Available at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### **Endpoints**
#### Generate Tracking Number
- **Method**: `GET /v1/api/next-tracking-number`
- **Parameters**:
    - `originCountryId` (string): Origin country code (e.g., `US`).
    - `destinationCountryId` (string): Destination country code (e.g., `IN`).
    - `weight` (decimal): Weight of the shipment.
    - `customerId` (UUID): Unique customer identifier.
    - `customerSlug` (string): Customer-specific slug.
- **Response**:
  ```json
  {
    "trackingNumber": "USIN123456789012",
    "createdAt": "2024-12-04T10:00:00Z",
    "status": "SUCCESS",
    "estimatedDelivery": "2024-12-07",
    "priority": "STANDARD"
  }
  ```

#### Retrieve Tracking Details
- **Method**: `GET /v1/api/tracking-details`
- **Parameters**:
    - `trackingId` (string): Unique tracking number.
- **Response**:
  ```json
  {
    "trackingNumber": "USIN123456789012",
    "createdAt": "2024-12-04T10:00:00Z",
    "status": "IN_TRANSIT",
    "estimatedDelivery": "2024-12-07",
    "priority": "STANDARD"
  }
  ```

---

## **Error Handling**
Error responses are standardized:
- **Validation Errors**:
  ```json
  {
    "originCountryId": "Origin country ID must not be blank",
    "weight": "Weight must be positive"
  }
  ```
- **Custom Exceptions**:
    - `TrackingIdNotFoundException`: Returns `404 Not Found`.
    - `DuplicateTrackingNumberException`: Returns `409 Conflict`.
    - `InvalidInputException`: Returns `400 Bad Request`.

---

## **Profiles and Environments**

- **Default Profile**: Uses H2 database for local development.
- **Production Profile**: Configures MySQL with credentials provided via environment variables.

---

## **Database Setup**

### **Development (H2 Database)**
No setup is required; the database is in-memory and auto-configured.

### **Production (MySQL)**
1. Create a database:
   ```sql
   CREATE DATABASE tracking_service;
   ```
2. Update credentials in `application-prod.properties`:
   ```properties
   spring.datasource.username=<your-username>
   spring.datasource.password=<your-password>
   ```

---

## **Cloud Deployment and Scalability**

### **Deployment Details**
The application is deployed on **AWS Elastic Beanstalk**, which provides managed hosting and scaling capabilities for the service.
- **Elastic Beanstalk**: Handles application deployment, monitoring, and environment scaling automatically.
- **MySQL RDS**: The application uses **Amazon RDS** (Relational Database Service) for reliable, high-performance database management.
    - **RDS Endpoint**: `tracking-service.c34csgeyszjc.ap-south-1.rds.amazonaws.com`
    - **Database**: MySQL
    - **Region**: `ap-south-1` (Mumbai)

### **Scalability**
The application is designed for **horizontal scalability** to handle increasing workloads:
- **Elastic Load Balancer (ELB)**: Distributes incoming traffic across multiple instances.
- **Auto Scaling**: Automatically adjusts the number of instances based on CPU utilization and traffic patterns.
- **Stateless APIs**: Built to ensure scalability by avoiding dependency on local storage for state persistence.
- **Database Scalability**: RDS supports vertical scaling (larger instance sizes) and read replicas for high read throughput.

### **Access**
You can access the deployed application using the following link:
[http://tracking-service.ap-south-1.elasticbeanstalk.com/swagger-ui/index.html](http://tracking-service.ap-south-1.elasticbeanstalk.com/swagger-ui/index.html)

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

## **Testing**
- Test cases are implemented for:
    - Service layer methods (e.g., tracking number generation).
    - Controller endpoints with mock data.
- To run tests:
  ```bash
  mvn test
  ```

---

## **Future Enhancements**
1. **Authentication and Authorization**:
    - Add JWT-based security for endpoints.
2. **Caching**:
    - Use Redis to cache frequently accessed tracking data.
3. **Third-Party Integration**:
    - Integrate APIs from logistics providers (e.g., FedEx, UPS).
4. **Enhanced Notifications**:
    - Add support for SMS or email updates.
5. **Enhanced Tracking**:
    - Include real-time updates on shipment locations.

This service provides a robust foundation for tracking and logistics management, designed to meet the growing demands of modern e-commerce and supply chain systems.

## Steps to Build and Run the Application Locally

1. **Clone the Repository**  
   Clone the repository to your local machine:
   ```bash
   git clone https://github.com/iambharath-ashok/tracking-service.git
   cd tracking-service
   ```

    2. **Set Up the Environment**  
       Ensure that you have the following prerequisites installed:
        - JDK 17 or later
        - Maven (latest stable version)
        - An IDE (e.g., IntelliJ IDEA, Eclipse, or VS Code) for editing and running the project

    3. **Build the Application**  
       Run the following Maven command to build the application:
       ```bash
       mvn clean install
       ```

    4. **Run the Application**  
       Use the following command to start the Spring Boot application:
       ```bash
       mvn spring-boot:run
       ```

       Alternatively, you can run the generated JAR file from the `target` directory:
       ```bash
       java -jar target/tracking-service-0.0.1-SNAPSHOT.jar
       ```

    5. **Access Swagger UI**  
       After starting the application, open your browser and navigate to:
       [http://localhost:8080/swagger-ui/index.htm](http://localhost:8080/swagger-ui/index.htm)

        6. **Check API Endpoints**  
           Use tools like Postman or Curl to interact with the available APIs. Refer to the Swagger documentation for detailed API descriptions.


This README provides comprehensive guidance for setting up, running, and extending the **Tracking Service** application. Let me know if any further adjustments are needed!