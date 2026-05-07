# 📏 Quantity Measurement Application (QMA) - Full Stack Microservices

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-orange.svg)

**Quantity Measurement App (QMA)** is a high-end, industrial-grade application designed for precision unit conversions and comparisons across Length, Weight, Volume, and Temperature. This project is built on a modern **Cloud-Native Architecture** featuring five microservices and a premium React-based workspace.

---

## 🚀 Live Environment
*   **Production Frontend:** [https://quantitymeasurementapp-p0gz.onrender.com](https://quantitymeasurementapp-p0gz.onrender.com)

*   **API Gateway:** Hosted on Port 4000 (Centralized Access Point)

*   **Interactive API Docs (Swagger):** http://localhost:4000/swagger 

---

## 🏗️ The Microservices Ecosystem

The system is architected as a distributed ecosystem to ensure high availability and modular scaling:

1.  **`QMA-Registry` (Discovery Server):** Uses Netflix Eureka. Every service registers here automatically so they can "talk" to each other without hardcoded URLs.
2.  **`QMA-API` (Gateway):** The single entry point for the frontend. It handles routing, CORS, and security filtering.
3.  **`QMA-Auth` (Identity Provider):** A specialized service for security. It handles JWT generation, User registration, and a **Modular Google OAuth2** integration.
4.  **`QMA-Service` (Business Logic):** The core calculation engine. It handles complex unit conversion mathematics and arithmetic operations (e.g., adding 1 liter to 1 gallon).
5.  **`QMA-Client` (Premium Frontend):** A state-of-the-art React application featuring:
    *   **Professional Split-Screen Auth:** A clean, high-end branding sidebar and centered login/signup forms.
    *   **Advanced Dashboard:** A unified workspace for real-time conversions and history tracking.

---

## ✨ Key Features

*   **⚡ Precision Conversions:** Highly accurate conversions between Inch/Feet/Yard, Kg/Gram/Ton, Litre/Gallon, and Temperature.
*   **📊 History Tracking:** Every conversion is automatically saved to your profile for future reference.
*   **🛡️ Multi-Method Auth:** Secure login via traditional Email/Password or the ultra-fast **Continue with Google** (OAuth2).
*   **🎨 Premium UI/UX:** A professional "Indigo & Teal" design language with responsive split-screen layouts and custom SCSS styling.
*   **⚙️ Modular Security:** A dedicated `OptionalGoogleOAuth2Config` class that separates social login logic for easier maintenance.
*   **🔗 Service-to-Service Communication:** Uses Feign Clients for secure data exchange between the Gateway, Auth, and Quantity services.

---

## 🛠️ Technology Stack

### **Backend (The Core)**
*   **Java 21 / Spring Boot 3.x**
*   **Spring Cloud:** Gateway, Netflix Eureka, OpenFeign.
*   **Database:** PostgreSQL (Cloud-hosted on Aiven).
*   **Security:** Spring Security, JWT (Stateless), Google OAuth2.
*   **Docs:** SpringDoc / OpenAPI (Swagger).

### **Frontend (The Experience)**
*   **React 18 / Vite** (Lightning-fast builds).
*   **State Management:** React Context API & Hooks.
*   **Styling:** Custom SCSS with a professional design system (Inter & Space Grotesk fonts).
*   **Icons:** Lucide-React.

---

## 📂 Project Organization

```text
QuantityMeasurementApp/
├── QMA-Registry/     # Eureka Service Discovery Server (Port 8761)
├── QMA-API/          # API Gateway (Port 4000)
├── QMA-Auth/         # Identity & Security Service (Port 5000)
├── QMA-Service/      # Core Mathematics & Models (Port 6000)
├── QMA-Client/       # Premium React Application (Port 5173)
├── run-all.bat       # Orchestration script to launch all services
└── README.md         # You are here!
```

---

## 🚦 Getting Started (Local Development)

### **1. Launching the Services**
You don't need to start every service manually. Use the provided automation script:
```powershell
./run-all.bat
```
This script will sequentially launch the Registry, API, Auth, Service, and Frontend.

### **2. Database Setup**
Ensure your `application.properties` in `QMA-Auth` and `QMA-Service` point to your PostgreSQL instance.
```properties
spring.datasource.url=jdbc:postgresql://your-db-url:port/quantity_measurement
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 👤 Author
**Kajal Wankhede**
*   **GitHub:** [@BL-KajalWankhede](https://github.com/BL-KajalWankhede)
*   **Portfolio:** [Project Link](https://quantitymeasurementapp-p0gz.onrender.com)


