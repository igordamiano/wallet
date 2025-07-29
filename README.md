
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

## 🧱 Technologies

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Validation
- SpringDoc OpenAPI (Swagger)
- In-memory data structures (for simplicity)

## 🧠 Design Decisions

- **Idempotency**: Deposit, withdraw, and transfer operations are idempotent to avoid duplicate processing.
- **DTOs**: Used to separate input/output data and encapsulate request structure.
- **Service layer**: Business logic is organized in service classes to ensure separation of concerns.
- **Validation**: Request bodies are validated using `@Valid` and Java Bean Validation annotations.
- **Exception Handling**: A global `@ControllerAdvice` captures and formats known exceptions into standardized responses.
- **Logging**: Errors and important events are logged with useful context for traceability.
- **Swagger**: API is documented using OpenAPI 3 with proper tags, summaries and response codes.

## 📝 Assumptions

- For simplicity, all data is stored in memory. In a real production scenario, a relational database would be used with transactional guarantees.
- Wallet operations assume that the user must already exist.
- Authentication is mocked or omitted for this challenge scope.

## 🧪 How to Test

You can test the endpoints using Swagger UI or via curl/Postman. Example curl:

```bash
curl -X POST http://localhost:8080/deposit   -H "Content-Type: application/json"   -d '{ "userId": 1, "amount": 100.0 }'
```

## ⏱ Time Spent

Estimated total time spent: ~6–8 hours

---

Made with 💻 by [Igor Damiano](https://github.com/igordamiano)
