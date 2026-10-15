# Order Management API

A customer management REST API built with **Java 21** and **Spring Boot 4.1.1**.

The application provides customer CRUD operations, stores customer information in **MongoDB**, and dynamically enriches customer queries with order references retrieved from an externals API.

The project follows **Hexagonal Architecture**, also known as **Ports and Adapters**, to keep the domain and application logic independent from HTTP, MongoDB, Spring infrastructure, Docker, and cloud deployment concerns.

---

## Table of Contents

- [Live Demo](#live-demo)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Clone the Repository](#clone-the-repository)
- [Run and Validate the Application Locally](#run-and-validate-the-application-locally)
- [Environment Variables](#environment-variables)
- [Run from IntelliJ IDEA](#run-from-intellij-idea)
- [Testing and Code Quality](#testing-and-code-quality)
- [Azure Deployment](#azure-deployment)

---

## Live Demo

The application is currently deployed to **Azure Container Apps**.

### Customer API

```text
https://ca-order-management-wp-dev.ashywater-483770a9.eastus2.azurecontainerapps.io/api/v1/customers
```

### Orders API

```text
https://ca-order-management-wp-dev.ashywater-483770a9.eastus2.azurecontainerapps.io/api/v1/orders
```

### Swagger UI

```text
https://ca-order-management-wp-dev.ashywater-483770a9.eastus2.azurecontainerapps.io/swagger-ui/index.html
```

> The Azure deployment is a demonstration environment. Availability and stored data may change when the Container App is restarted, redeployed, scaled, or removed.

---

## Technology Stack

### Core Platform

- **Java 21**
- **Spring Boot 4.1.1**
- **Apache Maven**

### Web and API

- **Spring Boot**
  - REST controllers
  - JSON request and response handling
  - Global exception handling
- **Spring RestClient**
  - Consumes the externals API
- **Jakarta Bean Validation**
  - Request body validation
  - Path variable validation
- **Springdoc OpenAPI 2.8.13**
  - OpenAPI specification
  - Swagger UI documentation

### Persistence

- **Spring Data MongoDB**
- **MongoDB 7**
- **MongoDB Compass**, optional graphical database client


### Testing and Code Quality

- **JUnit 5**
  - Automated unit and application testing
- **Mockito**
  - Mocks application ports and external dependencies in unit tests
- **JaCoCo 0.8.15**
  - Generates test coverage reports
  - Enforces a minimum total line coverage ratio of 80%
- **Maven Checkstyle Plugin 3.6.0**
  - Validates Java code style during Maven's `validate` phase

### Containerization and Cloud

- **Docker**
- **Docker Compose**
- **Azure Container Registry**
- **Azure Container Apps**


## Prerequisites

### Recommended Docker-Based Setup

Install:

- Git
- Docker Desktop
- Docker Compose support

The Docker-based setup does **not** require a local Java installation or a local MongoDB installation to run the complete application.

### Optional Local Development Setup

To run or debug the Spring Boot application directly from the host, also install:

- Java Development Kit 21
- IntelliJ IDEA or another Java IDE
- MongoDB Compass, optional
- Postman, optional
- Azure CLI, only required for Azure deployment

---

## Clone the Repository

```bash
git clone <repository-url>
cd order-managment-lv
```

Replace `<repository-url>` with the HTTPS or SSH URL of this repository.

Verify the current branch:

```bash
git branch --show-current
```

---

## Run and Validate the Application Locally

The recommended local execution method is Docker Compose. This starts both the Spring Boot API and MongoDB.

### 1. Build and Verify the Project

```bash
./mvnw clean verify
```

The command must finish with:

```text
BUILD SUCCESS
```

This command validates code style, compiles the application, runs automated tests, checks coverage, and creates the executable JAR.

### 2. Confirm the Generated JAR

```bash
ls -lh target/*.jar
```

### 3. Start the Application

```bash
docker compose up -d --build
```

Docker Compose starts:

- `order-management-api` on host port `8080`
- MongoDB in a separate container
- MongoDB on host port `27017` for tools such as MongoDB Compass
- A persistent MongoDB volume
- A MongoDB health check

Inside the Docker Compose network, the API connects to MongoDB through:

```text
mongodb://mongo:27017/order_management_db
```

### 4. Verify the Containers

```bash
docker compose ps
```

Expected services:

```text
mongo                  healthy
order-management-api   running
```


## Environment Variables

The application reads environment-specific configuration from environment variables.

### `MONGODB_URI`

MongoDB connection string.

Docker Compose value:

```text
mongodb://mongo:27017/order_management_db
```

When running Spring Boot directly from the host:

```text
mongodb://localhost:27017/order_management_db
```

Azure value when the API and MongoDB run in the same Container App:

```text
mongodb://localhost:27017/order_management_db
```

### `MOCK_API_BASE_URL`

Base URL of the externals API.

```text
https://6994a4eab081bc23e9c0f61e.mockapi.io/api/v1
```

Environment-specific URLs must not be hard-coded in Java source code.

---

## Run from IntelliJ IDEA

This mode is useful for debugging Java code while keeping MongoDB in Docker.

### 1. Start Only MongoDB

```bash
docker compose up -d mongo
```

### 2. Configure IntelliJ Environment Variables

Open the application's Run Configuration and add:

```text
MONGODB_URI=mongodb://localhost:27017/order_management_db
MOCK_API_BASE_URL=https://6994a4eab081bc23e9c0f61e.mockapi.io/api/v1
```

### 3. Run the Application

Run:

```text
OrderManagmentApplication
```

Alternatively, use the Maven Wrapper:

```bash
MONGODB_URI=mongodb://localhost:27018/order_management_db \
MOCK_API_BASE_URL=https://6994a4eab081bc23e9c0f61e.mockapi.io/api/v1 \
./mvnw spring-boot:run
```

> Do not run the API container and the IntelliJ application at the same time unless one of them uses a different HTTP port. Both use port `8080` by default.

---

## Testing and Code Quality

The project uses JUnit 5 and Mockito for automated testing.

### Run the Complete Verification Lifecycle

```bash
./mvnw clean verify
```

This command performs the following operations:

1. Cleans previous build artifacts.
2. Validates Java source code with Checkstyle.
3. Compiles the source code.
4. Executes automated tests with JUnit 5.
5. Uses Mockito for isolated dependency testing where applicable.
6. Collects test coverage with JaCoCo.
7. Verifies that total line coverage is at least 80%.
8. Generates the executable Spring Boot JAR.

A successful execution ends with:

```text
BUILD SUCCESS
```



## Azure Deployment

The cloud deployment uses:

- Azure Container Registry
- Azure Container Apps
- One Spring Boot API container
- One MongoDB sidecar container
- Environment variables for MongoDB and the external Orders API

