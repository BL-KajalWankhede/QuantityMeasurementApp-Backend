# Quantity Measurement Application (QMA)

A robust, microservices-based application designed for precision unit conversions and comparisons. This project demonstrates a modern cloud-native architecture using Spring Cloud, React, and PostgreSQL.

## 🚀 Live Demo
**Frontend (React):** [https://quantitymeasurementapp-p0gz.onrender.com](https://quantitymeasurementapp-p0gz.onrender.com)

---

## 🏗️ Architecture Overview

The application is built using a **Microservices Architecture** to ensure scalability, fault tolerance, and independent deployment.

- **Service Registry (QMA-Registry):** Powered by Netflix Eureka for dynamic service discovery.
- **API Gateway (QMA-API):** Built with Spring Cloud Gateway for centralized routing, load balancing, and security.
- **Auth Service (QMA-Auth):** Handles user registration, JWT-based authentication, and Google OAuth2 integration.
- **Quantity Service (QMA-Service):** Core business logic for unit conversions (Length, Weight, Volume, Temperature).
- **React Client (QMA-Client):** A premium, responsive dashboard built with Vite, React, and Tailwind CSS.

---

## 📂 Project Structure

```text
QuantityMeasurementApp/
├── QMA-Registry/     # Eureka Service Discovery Server
├── QMA-API/          # Spring Cloud API Gateway
├── QMA-Auth/         # User Authentication & OAuth2 Service
├── QMA-Service/      # Core Quantity Measurement Service
├── QMA-Client/       # React (Vite) Frontend Application
├── QMA-Frontend/     # Vanilla JS Frontend (Legacy)
└── run-all.bat       # script for local dev
```

---

## 🛠️ Tech Stack

### **Backend**
- **Framework:** Spring Boot 3.x
- **Microservices:** Spring Cloud (Gateway, Eureka, OpenFeign)
- **Security:** Spring Security, JWT, OAuth2 (Google)
- **Database:** PostgreSQL (Hosted on Aiven Cloud)
- **Documentation:** SpringDoc / Swagger UI

### **Frontend**
- **React (Vite):** Modern UI with hooks and context API.
- **Styling:** Vanilla CSS / SCSS (Custom Premium Design).
- **Icons:** Lucide-React.

---

## 🔑 Environment Variables

To run this project in production or a different local environment, ensure the following variables are configured in your `application.properties` or system environment:

### **Backend (Auth & Service)**
| Variable | Description |
| :--- | :--- |
| `QMA_DB_URL` | PostgreSQL connection string (Aiven/Local) |
| `QMA_DB_USERNAME` | Database username |
| `QMA_DB_PASSWORD` | Database password |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID |
| `GOOGLE_CLIENT_SECRET`| Google OAuth2 Client Secret |
| `QMA_JWT_SECRET` | Secret key for signing JWT tokens |
| `QMA_FRONTEND_ORIGIN` | URL of the React frontend (for CORS) |

### **Frontend (React)**
| Variable | Description |
| :--- | :--- |
| `VITE_API_BASE_URL` | The URL of the API Gateway (Port 4000) |

---

## ✨ Features

- **Unit Conversion:** Convert between various units in Length (Inch, Feet, Yard), Weight (Kg, Gram, Ton), Volume (Litre, Gallon, Ml), and Temperature.
- **Arithmetic Operations:** Add or subtract quantities of the same type (e.g., 1 Gallon + 3.78 Litres).
- **Comparison:** Check equality between different unit representations.
- **User Dashboard:** Secure login and history tracking for all measurement operations.
- **Social Login:** Quick access via Google OAuth2.
- **Service Discovery:** Automatic registration and health monitoring via Eureka.

---

## 🚦 Getting Started

### **Prerequisites**
- Java 21+
- Node.js 18+
- Maven 3.9+
- Docker (Optional)

### **Local Execution**
The project includes a convenient script to launch all services simultaneously:

1. Clone the repository:
   ```bash
   git clone https://github.com/BL-KajalWankhede/QuantityMeasurementApp.git
   cd QuantityMeasurementApp
   ```
2. Run the orchestration script:
   ```bash
   ./run-all.bat
   ```

### **Service Ports**
| Service | Port | Description |
| :--- | :--- | :--- |
| `QMA-Registry` | 8761 | Eureka Dashboard |
| `QMA-API` | 4000 | API Gateway / Swagger |
| `QMA-Auth` | 5000 | Auth Microservice |
| `QMA-Service` | 6000 | Quantity Microservice |
| `QMA-Client` | 5173 | React Frontend |

---

## 🐳 Docker Deployment

Each microservice is containerized. To build and run using Docker:

```bash
# Build a service (example: Auth)
cd QMA-Auth
docker build -t qma-auth .
docker run -p 5000:5000 qma-auth
```

---

## 📖 API Documentation

Once the services are running, you can access the interactive Swagger documentation at:
`http://localhost:4000/swagger`

---

## 👤 Author
**Kajal Wankhede**
- GitHub: [@BL-KajalWankhede](https://github.com/BL-KajalWankhede)
---
