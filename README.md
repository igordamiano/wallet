
# Wallet Service Assignment

This project is a solution for the Wallet Service Assignment provided by RecargaPay. It simulates a digital wallet service where users can create accounts, deposit funds, withdraw money, and transfer funds between wallets.

## 📌 Requirements

- Java 21
- Maven 3.9+
- Docker (optional, for containerization)

## 🚀 Getting Started

### Clone the repository

```bash
git clone https://github.com/igordamiano/wallet.git
cd wallet
```

### Run the project

```bash
./mvnw spring-boot:run
```

The application will start on: `http://localhost:8080`

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## ✅ Features

- Create user account
- Deposit funds into wallet (idempotent)
- Withdraw funds from wallet (idempotent)
- Transfer funds between users (idempotent)
- Retrieve user by ID
- Check current wallet balance
- Check historical wallet balance
- Authenticate and generate JWT token
- Protect endpoints with JWT
- Return traceId in error responses and logs

## 🔐 Authentication

The API uses **JWT tokens** to authenticate users. First, create a user and then call `/auth/login?username={name}` to receive a token. Use this token in the `Authorization` header for protected endpoints:

```
Authorization: Bearer <token>
```

Endpoints that require authentication include:
- `/user/{id}`
- `/user/{id}/balance`
- `/deposit`
- `/withdraw`
- `/transfer`

### Swagger and JWT

Swagger UI includes a lock icon (🔐) for secured endpoints. Click it and paste your JWT token (e.g. `Bearer eyJhbGciOiJIUzI1NiIs...`) to authorize your session and test secured endpoints directly from the browser.

## 🧱 Technologies

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Validation
- Spring Security + JWT
- SpringDoc OpenAPI (Swagger)
- Logback + MDC
- H2 Database (in-memory)

## 🧠 Design Decisions

- **Idempotency**: Deposit, withdraw, and transfer operations are idempotent to avoid duplicate processing.
- **DTOs**: Used to separate input/output data and encapsulate request structure.
- **Service layer**: Business logic is organized in service classes to ensure separation of concerns.
- **Validation**: Request bodies are validated using `@Valid` and Java Bean Validation annotations.
- **Exception Handling**: A global `@ControllerAdvice` captures and formats known exceptions into standardized responses.
- **Logging**: Errors and important events are logged with useful context for traceability.
- **Trace ID**: Each request generates a unique traceId injected into the logs and returned in error responses.
- **JWT Authentication**: Stateless token-based auth with expiration and filter-based verification.
- **Swagger**: API is documented using OpenAPI 3 with proper tags, summaries, and response codes.

## 📎 Trace ID

Each HTTP request generates a `traceId` that:
- Is logged in every log entry for that request
- Is returned in the response header `X-Trace-Id`
- Is included in the error body (`ApiErrorDTO`) to assist with debugging and correlation

Example log:
```
2025-07-30 14:20:04.817 [8b72ca07-4611-4116-b690-5d4563c1cc23] INFO  WalletService - TESTE DE TRACE
```

Example error response:
```json
{
  "error": "User not found",
  "path": "/user/999",
  "timestamp": "2025-07-30T14:22:18",
  "traceId": "8b72ca07-4611-4116-b690-5d4563c1cc23"
}
```

## 🧪 How to Test

You can test the endpoints using Swagger UI or Postman.

```bash
curl -X POST http://localhost:8080/deposit \
  -H "Authorization: Bearer <your-jwt>" \
  -H "Content-Type: application/json" \
  -d '{ "userId": 1, "amount": 100.0, "operationId": "op-123" }'
```

## ⏱ Time Spent

Estimated total time spent: ~10–12 hours

---

Made with 💻 by [Igor Damiano](https://github.com/igordamiano)
