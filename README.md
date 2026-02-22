# 🚗 Lavugio

Lavugio is a full-stack ride-sharing platform built as a university project for the MRS course (2025/2026). Inspired by services like Uber and Bolt, it aims to deliver a complete end-to-end transportation experience across web and mobile.

Passengers can request rides instantly or schedule them in advance, track their driver in real time on an interactive map, chat with them directly in the app, and leave a review once the ride is done. Drivers get a dedicated interface for managing their availability, accepting incoming rides, and browsing their earnings history. Administrators have a full control panel for overseeing the platform — managing users, approving driver profile update requests, setting vehicle pricing, and viewing detailed ride analytics and reports.

The platform is built around real-time communication: driver locations are streamed live, ride status updates are pushed via WebSockets, and urgent panic alerts from active rides are broadcast instantly to admins. Firebase handles push notifications so drivers and passengers stay informed even when the app is in the background.

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4 (Java 21), PostgreSQL, JPA/Hibernate |
| Frontend | Angular 21, Angular Material, TailwindCSS, Leaflet |
| Mobile | Android (Kotlin, Jetpack Compose) |
| Real-time | WebSockets (STOMP/SockJS), Firebase push notifications |
| Auth | Spring Security + JWT |
| Maps/Routing | OSRM, Leaflet Routing Machine |

## ✨ Features

- 🧑‍💼 **Passengers** — Book on-demand or scheduled rides, track in real-time, chat with drivers, review rides, save favorite routes
- 🚘 **Drivers** — Manage availability, accept rides, view ride history and earnings reports
- 🛡️ **Admins** — Manage users, approve driver update requests, configure pricing, view analytics and reports
- 🔔 **Notifications** — Real-time WebSocket alerts and Firebase push notifications
- 🚨 **Panic button** — Emergency alerts during active rides, broadcast to admins instantly

## 🚀 Getting Started

### Prerequisites

- Java 21, Maven
- Node.js, npm
- PostgreSQL 14+

### ⚙️ Backend

```bash
cd lavugio-back

# Set up database
psql -U postgres -c "CREATE DATABASE lavugio_db;"
psql -U postgres -d lavugio_db -f src/postgresql/init_script.sql
psql -U postgres -d lavugio_db -f src/postgresql/data.sql

# Run
mvn spring-boot:run
```

Runs on `http://localhost:8080`. API docs available at `/swagger-ui.html`.

### 🌐 Frontend

```bash
cd lavugio-front
npm install
npm start
```

Runs on `http://localhost:4200`.

### 📱 Mobile

Open `lavugio-mobile/` in Android Studio and run on an emulator or device.

## 📁 Project Structure

```
lavugio-back/      # Spring Boot REST API
lavugio-front/     # Angular web app
lavugio-mobile/    # Android app
```

## 🧪 Running Tests

```bash
# Backend (unit + integration)
cd lavugio-back && mvn test

# Frontend
cd lavugio-front && npm test
```
