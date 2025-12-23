# BPlot Backend

A social platform where users can rate books, search by taste, get recommendations, create lists, and interact socially — built with a modern microservices architecture using Java, Spring Boot, and cloud‑ready infrastructure.

---

## 🌟 Overview
BPlot is the backend of a full social reading platform. It provides secure authentication, book management, user profiles, social features, ratings, and scalable microservices prepared for cloud deployment. This backend is written entirely in **Java 17**, using **Spring Boot 3** and modern distributed‑systems patterns.

> **Note:** This repository is proprietary and not intended for reuse, modification, or redistribution. See the license section below.

---

## 🚀 Main Features
### 🔐 Authentication & Security
- User registration and login
- JWT authentication
- Role‑based authorization
- API Keys
- Password encryption (BCrypt)
- Secure profile with user image

### 📚 Books
- CRUD for books *(parts pending)*
- Ratings + average score
- Reviews/comments
- Search by title and author
- Search by genres
- Personalized recommendations *(planned)*
- User lists (favorites, must‑read, etc.)

### 👥 Social Interaction
- Follow/unfollow users
- Cross‑service notifications (Kafka events)
- Notification microservice *(planned)*

### 🏗️ Infrastructure & Architecture
- Fully functional microservices:
  - **Security Microservice**
  - **Users Microservice**
  - **Book Microservice**
  - **Social Microservice**
- Redis caching
- Kafka event streaming
- Circuit Breakers (Resilience4j)
- JPA/Hibernate ORM
- REST API communication (sync via RestClient)
- Dockerized services + Docker Compose
- CI pipeline: GitHub Actions
- Continuous Delivery‑ready configuration

---

## 🧰 Tech Stack
**Backend Languages & Frameworks:**
- Java 17
- Spring Boot 3.x
- Spring Security
- JPA / Hibernate
- REST APIs
- Resilience4j

**Infrastructure:**
- Docker & Docker Compose
- Kafka
- Redis
- Maven
- GitHub Actions

**Architecture:**
- Microservices
- Distributed communication
- Event-driven patterns
- Circuit breakers
- Outbox pattern 

---

## 🧪 Testing
- Unit testing with **JUnit 5**
- Mocking with **Mockito**
- Integration testing in multiple microservices
- Automated tests executed in CI before build

---

## 🖼️ Architecture Diagrams
### Microservices Architecture
<img width="711" height="436" alt="microservices_bplot" src="https://github.com/user-attachments/assets/b729020c-da55-4749-8558-5b32ee5e4b64" />


### AWS Deployment Diagram
<img width="696" height="451" alt="aws_bplot" src="https://github.com/user-attachments/assets/d4d56586-bfc9-4d5c-8a58-2d4c679bc54e" />


---

## 🗺️ Roadmap
Planned and in-progress features:
- Email verification for new users
- Notification microservice
- Recommendation system with ML
- Full deployment on AWS
- Complete Redis caching across all services
- Kafka + Outbox fully implemented everywhere needed
- Complete test coverage for all microservices

---

## 📜 License (Proprietary / All Rights Reserved)
This project is **proprietary** and **all rights are reserved**. No part of this codebase may be used, copied, modified, merged, published, distributed, sublicensed, or sold without explicit written permission from the author.

```
Copyright (c) 2025 Robert Kovachev
All Rights Reserved.

Unauthorized copying, distribution, modification, or use of this software, via any medium, is strictly prohibited.


