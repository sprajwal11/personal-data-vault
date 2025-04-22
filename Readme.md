# 🔐 Personal Data Vault

A lightweight, self-hosted **password manager** and **secure digital vault** for storing secrets, personal notes, and files — all **encrypted at rest**, with strong authentication and full audit trails.

---

## 📌 About

This project provides a secure backend API for managing sensitive personal data. Think of it as a hybrid of a password manager + encrypted file locker. Designed with modern security principles and built with **Spring Boot**, **JWT**, and **PostgreSQL**.

---

## 🚀 Features

### 👤 User Management

- ✅ **User Registration & Login** (with password hashing)
- 🔐 **JWT-based Authentication**
- 👥 **Role-Based Access**: Admin / User
- 🔄 Optional: **Multi-Factor Authentication (MFA)** (e.g., TOTP)

### 🔐 Vault Core

- 📂 **Secrets Manager**: Store text-based items (passwords, license keys, notes)
- 📁 **Secure File Uploads**: Encrypt and store PDF/images/docs using AES-256
- 🗂️ **Categories**: Organize secrets into folders like "Work", "Finance", etc.
- 🔎 **Search**: Query secrets by label/tags (non-encrypted metadata)
- 🛡️ **AES Encryption at Rest**: All sensitive data securely encrypted

### 📜 Audit & Logging

- 📅 **User Activity Log**: Record actions like login, upload, delete, etc.
- 🖥️ **Admin Dashboard (future)**: View system logs and stats
- 🌍 **Geo/IP Tracking** (future enhancement)

[//]: # (### 🛡️ Security)

[//]: # ()
[//]: # (- 🚫 **Rate Limiting**: Protect from brute-force attacks)

[//]: # (- 🔐 **Password Policies**: Enforce strong passwords)

[//]: # (- 🔓 **Session Management**: Control session lifespan)

[//]: # (- ⏳ **Auto-expiring Files**: Download links can auto-expire)

---

## 🏗️ Architecture

```text
                     
                      +------------------------+
                      |   Spring Boot Backend  |
                      |   (Vault App Core)     |
                      +-----------+------------+
                                  |
       +----------------+--------+------------------+
       |                |                           |
+--------------+ +-------------------+ +--------------------+
| Auth Service | | Vault Secret Logic| | Vault File Logic   |
|  - JWT, RBAC | |  - Encrypt Secrets| |  - AES Encrypted   |
|  - BCrypt    | |  - CRUD Secrets   | |  - File Storage    |
+--------------+ +-------------------+ +--------------------+
                                  |
                                  v
                      +------------------------+
                      |     PostgreSQL DB      |
                      |  - Users, Logs, Files  |
                      +------------------------+



```
---

## 🛠️ Setup & Development

### 🔧 Tech Stack
- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **PostgreSQL**
- **Log4j2**
- **Swagger / OpenAPI**
- **JUnit + Mockito**

---

### ⚙️ Prerequisites

Make sure you have the following installed:

- Java 17+
- PostgreSQL (or Docker)
- Maven
- Postman (optional, for testing APIs)

---

### ▶️ Running the App

```bash
# Clone the repository
git clone https://github.com/sprajwal11/personal-data-vault
cd personal-data-vault

# Configure your PostgreSQL connection in application.yml or application.properties

# Run Docker-Compose(for PostgreSQL)
docker-compose up -d

# Run with Maven
./mvnw spring-boot:run
```
### 📂 API Documentation
- Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html` after starting the app.
- ✅ Use the /auth/login endpoint to get your JWT
- 🔐 Then click “Authorize” in Swagger and paste your token with Bearer <your_token>

