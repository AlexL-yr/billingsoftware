# Retail Billing Software

A full-stack retail billing system built with Spring Boot and React, supporting real-time order processing, Stripe payment integration, role-based access control, and revenue analytics.

## Features

- **Order Management** — Create and manage orders with cash or Stripe payment support
- **Stripe Integration** — Sandbox payment processing with webhook-based order verification
- **Role-Based Access Control** — Admin and standard user roles with JWT authentication
- **Revenue Analytics** — Daily sales summaries and recent order tracking via ECharts dashboard
- **Image Storage** — Product images and promotional posters stored on AWS S3
- **Invoice Generation** — Browser-based receipt printing for both payment types
- **CI/CD Pipeline** — Automated build, test, and deployment with Jenkins and Docker

## Tech Stack

**Backend**
- Java 17, Spring Boot, Spring Security + JWT
- JPA (Hibernate), MySQL
- Stripe API (Sandbox)
- JUnit 5, Mockito (80%+ test coverage)
- Docker, Jenkins

**Frontend**
- React, Redux, Axios
- ECharts, Ant Design

**DevOps & Cloud**
- Docker, Jenkins CI/CD
- AWS S3

## Architecture
