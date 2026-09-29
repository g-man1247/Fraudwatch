# FraudWatch — Basic Transaction Anomaly Flagging System

## 1. Project Description
FraudWatch is a Spring Boot backend application designed to detect suspicious transactions in a digital wallet service. It evaluates each transaction against configurable anomaly detection rules (like unusually large amounts or high frequency) and flags them for manual review before completion.

## 2. Technologies
* Java 17
* Spring Boot 3.2.4
* Spring Web, Spring Data JPA, Jakarta Validation
* MySQL
* Maven
* JUnit 5, Mockito

## 3. Architecture
The project follows a standard n-tier architecture:
* **Controllers** handle HTTP requests and JSON mapping.
* **Services** contain core business rules (e.g., `FraudDetectionService`, `TransactionService`, `ReviewService`).
* **Repositories** interact with the MySQL database using Spring Data JPA.
* **Database** stores domain entities.

## 4. Database Design / UML
* **Account**: Sender and receiver details.
* **Transaction**: The core event. (1-to-Many to FlaggedTransaction)
* **Rule**: Configurable anomaly detection rule (e.g., LARGE_AMOUNT).
* **FlaggedTransaction**: A join entity representing which rule flagged which transaction. This allows multiple rules to flag a single transaction.
* **ReviewOutcome**: Stores the manual review outcome (APPROVED or BLOCKED) for a flagged transaction.

## 5. Setup & Run
**MySQL Config**
Create a database named `fraudwatch`. Configure `application.yml` or set environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

**Run**
`mvn spring-boot:run`

**Test**
`mvn test`

## 6. Endpoints
* `POST /api/transactions`
* `GET /api/transactions`
* `POST /api/rules`
* `GET /api/rules`
* `GET /api/flagged-transactions`
* `PUT /api/flagged-transactions/{id}/review`
* `GET /api/dashboard/summary`
