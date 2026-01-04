# Spring Boot Application Architecture

This **Spring Boot application** uses both **MVC and REST controllers**.  
- **Thymeleaf templates** are used for the **Admin** and **Doctor** dashboards.  
- **REST APIs** serve all other modules.  
- The application interacts with two databases:  
  - **MySQL** – stores patient, doctor, appointment, and admin data.  
  - **MongoDB** – stores prescription data.  

All controllers route requests through a **common service layer**, which delegates to the appropriate repositories.  
MySQL uses **JPA entities**, while MongoDB uses **document models**.

---

## 1. Dashboards and REST Modules

- Upon entering the application, users are redirected to an **authentication (login) page**.  
- The **Patient Dashboard** is an HTML page where patients can:
  - Book an appointment.  
  - View prescriptions.  

### Appointment REST Module
- Provides endpoints to **Create**, **Read**, **Update**, and **Delete** appointments.  
- Used in both the **Patient Dashboard** and **Doctor Dashboard** to display or manage appointments.

### Patient Record REST Module
- Returns a patient’s **past appointments** and **prescriptions**.  
- Exposes primarily **GET** endpoints to ensure **data integrity**.

---

## 2. Controllers

- After authentication:
  - **Admin** and **Doctor** users are routed to their respective **Thymeleaf dashboards**.  
  - **Spring Security** ensures that each user type accesses the correct dashboard.  
- The **REST modules** send their requests to **REST controllers**, which determine which **service** to call.  

---

## 3. Service Layer

Both MVC and REST controllers delegate logic to **service classes**.  
The following services are defined:

- **DoctorService** – Manage doctors  
- **AdminService** – Manage admins  
- **PatientService** – Manage patients  
- **AppointmentService** – Manage appointments  
- **PrescriptionService** – Manage prescriptions  
- **PatientRecordService** – Manage patient records  

Each service encapsulates business logic and interacts with the appropriate repository.

---

## 4. Repositories

The application uses both **SQL (MySQL)** and **NoSQL (MongoDB)** repositories.

### SQL Repositories (MySQL)
- **DoctorRepository**  
- **AdminRepository**  
- **PatientRepository**  
- **AppointmentRepository**

Each provides standard **CRUD operations** using **Spring Data JPA**.

### NoSQL Repository (MongoDB)
- **PrescriptionRepository** – manages prescription documents.

---

## 5. Databases

- The repositories interface directly with the databases.  
- Databases will be **pre-populated** during early development.  
- Spring Boot’s **auto-update** feature can be enabled to automatically:
  - Create database tables.
  - Populate them from entity definitions.

---

## 6. Database Models

- MySQL tables will be represented by **JPA entity models**.  
- Each entity corresponds to a table and defines relationships where applicable.

---

## 7. JPA Entity Management

- Each repository manages a **JPA entity** or **MongoDB document**.  
- Entities are used by the repositories to **persist and retrieve** data via **Spring Data JPA**.  
- MongoDB document models are used similarly for prescriptions.
