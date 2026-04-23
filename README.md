# Real-Time Flight Radar Dashboard

Built as an end-to-end distributed system, this application orchestrates containerized microservices, handles real-time geospatial data ingestion via robust backend object mapping, and serves live telemetry to a reactive component-driven user interface.

## Technologies Used

- **Frontend**: React (Vite), React Leaflet, Vanilla CSS (Glassmorphism design)
- **Backend**: Java 11, Spring Boot 2.7, Spring Data JPA, Hibernate Spatial
- **Database**: PostgreSQL with PostGIS extension
- **Cloud/DevOps**: Docker Compose

## Features

- **Real-time Spatial Tracking**: Simulates transit vehicle movement and updates their coordinates in real-time.
- **REST APIs**: Java backend serving data via optimized DTOs.
- **Dynamic Map UI**: Modern React frontend that plots the spatial data automatically using Leaflet.
- **Premium Aesthetics**: Styled with custom CSS utilizing modern design tokens, animations, and glassmorphism.

## How to Run

### Prerequisites
- Docker and Docker Compose
- Java 11
- Node.js 18+

### 1. Start the Database
From the root directory (`ibm-gis-project`), run:
```bash
docker-compose up -d
```
This starts the PostGIS database on port 5432.

### 2. Start the Backend
Navigate to the `backend` directory and start the Spring Boot application:
```bash
cd backend
./mvnw spring-boot:run
```
The REST API will be available at `http://localhost:8080/api/transit`.

### 3. Start the Frontend
Navigate to the `frontend` directory, install dependencies, and start the development server:
```bash
cd frontend
npm install
npm run dev
```
Open your browser and navigate to `http://localhost:5173/` to view the application!
