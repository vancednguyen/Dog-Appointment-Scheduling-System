Dog Service Booking System
Overview

This project is a Spring Boot web application that allows users to book pet care services from registered providers. The system supports appointment scheduling, provider availability management, and a mock notification system to simulate distributed service communication.

The application follows a layered monolithic architecture with a clear separation of concerns between presentation, business logic, and data access. It also demonstrates optimistic concurrency control and a coarse-grained REST-based service boundary.

Features
User registration and login
Provider registration and management
Service offering management
Availability slot creation and scheduling
Appointment booking and cancellation
Optimistic locking to prevent double booking
Mock notification system using REST
Thymeleaf-based user interface

Architecture
The system is implemented as a layered monolith:
Presentation Layer
Spring MVC controllers and Thymeleaf templates handle user interaction and HTTP requests.
Service Layer
Business logic is implemented in service classes such as booking, user management, and provider operations.
Data Access Layer
Repository classes use manual JDBC with handwritten SQL queries to interact with a MySQL database.
External Service Boundary (Mock)
A mock notification system is exposed through a REST controller to simulate a distributed service.

Technology Stack
Java
Spring Boot (Web MVC)
Thymeleaf
MySQL
JDBC (manual SQL, no ORM)
Maven

Concurrency Handling
The system uses optimistic locking to prevent double booking of availability slots.
Each slot contains a version field. When a booking is attempted, the system performs a conditional update: The slot must still be available.
The version must match the previously read value
If the update fails, the booking is rejected, ensuring that only one user can successfully book a slot.

Transaction Design
Booking creation is handled within a transactional service method to ensure atomicity.
Slot reservation and appointment creation are treated as a single unit of work.
Some update and cancellation flows are implemented as multiple operations and can be improved with stronger transactional boundaries.

Notification System (Mock Distributed Boundary)
The application includes a mock notification subsystem to demonstrate a distributed service boundary.

The booking system calls a REST endpoint via a client service.
A separate controller simulates the notification service.
Communication is done using JSON-based request and response DTOs.

This design separates responsibilities:
Booking system decides when to send notifications
Notification system handles message processing
System Management and Logging
Basic system management is implemented using health and monitoring controllers.
Logging is handled through Spring Boot’s default logging system.
Logs are used for debugging booking operations, service calls, and notification requests.

Limitations
The notification system is simulated within the same application rather than deployed separately.
No real email or SMS integration.
Transaction handling is not fully consistent across all workflows.
Limited concurrency protection outside of booking creation.
Database credentials are hardcoded in configuration.
Logging and monitoring are basic and not production-grade.
Limited error handling and validation.
No automated testing included.

Future Improvements
Deploy notification system as a separate service
Integrate real notification providers (email/SMS)
Standardize transaction management using Spring
Improve concurrency handling in all workflows
Add Spring Boot Actuator for monitoring and metrics
Implement global exception handling
Add automated unit and integration tests
Improve UI/UX and validation
Introduce role-based security with Spring Security
Externalize configuration using environment variables

How to Run
Ensure MySQL is installed and running.
Create the required database and tables.
Update application.properties with your database credentials.
Build the project:
mvn clean install
Run the application:
mvn spring-boot:run
Open a browser and navigate to:
http://localhost:8080

Project Structure
controller/     - Spring MVC controllers
service/        - Business logic
repository/     - JDBC data access
model/          - Domain models
dto/            - Data transfer objects
config/         - Configuration classes
templates/      - Thymeleaf HTML views
