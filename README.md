# Banking Customer Management Microservice

A complete, production-ready Spring Boot 3 & Java 21 RESTful Microservice with In-Memory H2 Database, Bean Validation, OpenAPI/Swagger 3 documentation, and a clean Web Frontend Portal.

---

## 🚀 Quick Localhost Links & Tools

Once your Spring Boot application is running on port 8055, open these URLs in your browser:

| Service / Tool | Localhost URL | What It Does & How To Use |
| :--- | :--- | :--- |
| **Web Management Portal** | http://localhost:8055/index.html | The customer directory GUI. Add/edit/delete customers, search by city, view all customers, and inspect real-time validation error responses. |
| **Swagger UI (OpenAPI Docs)** | http://localhost:8055/swagger-ui/index.html | Interactive API documentation. Test all GET, POST, PUT, DELETE endpoints with live request/response inspection right in your browser. |
| **OpenAPI 3.0 JSON Spec** | http://localhost:8055/v3/api-docs | Raw OpenAPI schema definition in JSON format (can be imported into Postman or Insomnia). |
| **H2 Database Console** | http://localhost:8055/h2-console | Built-in database browser. Inspect and query the in-memory SQL CUSTOMERS table directly. |

---

### 🔑 H2 Database Login Credentials
When opening http://localhost:8055/h2-console, enter these exact credentials:
- **Driver Class:** org.h2.Driver
- **JDBC URL:** jdbc:h2:mem:bankingdb
- **User Name:** sa
- **Password:** (leave blank / empty)
- Click **Connect** (or Test Connection).
- In the SQL editor on the right, run: SELECT * FROM CUSTOMERS; to inspect saved rows!

---

## 📡 REST API Reference

Base URL: http://localhost:8055/api/customers

| Method | Endpoint | Description | Sample Request / Query Params |
| :--- | :--- | :--- | :--- |
| GET | /api/customers | Fetch all customers | - |
| GET | /api/customers?city=Dallas | Search customers by city (case-insensitive) | ?city=Dallas |
| GET | /api/customers/{id} | Get customer by ID | /api/customers/1 |
| POST | /api/customers | Create a new customer | JSON Body (see payload below) |
| PUT | /api/customers/{id} | Update existing customer by ID | /api/customers/1 + JSON Body |
| DELETE | /api/customers/{id} | Delete customer by ID | /api/customers/1 |

---

### 📦 Sample JSON Payload (POST / PUT)

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@bank.com",
  "phoneNumber": "+1-555-0199",
  "nationalId": "SSN-9988-1122",
  "city": "Dallas",
  "address": "1200 Elm Street, Suite 400",
  "accountStatus": "ACTIVE",
  "accountBalance": 2500.50
}
```

---

## 🛠️ How to Run the Application

### Option A: From IDE (IntelliJ IDEA, Eclipse, VS Code)
1. Open the project folder in your IDE.
2. Locate src/main/java/com/banking/customer/CustomerApplication.java.
3. Right-click and select Run 'CustomerApplication' (or click ▶ Run).
4. Watch the console until you see: "Started CustomerApplication in X.XXX seconds".
5. Open http://localhost:8055/index.html in your browser.

### Option B: From Command Line / Terminal
```bash
# Build and run with Maven Wrapper
./mvnw spring-boot:run

# Or with installed Maven
mvn spring-boot:run
```

---

## TODO: Work on test cases
```
