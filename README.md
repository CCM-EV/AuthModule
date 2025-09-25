# AuthModule

Authentication Module - Spring Boot OAuth2 Authorization Server

## Overview
This is a Spring Boot based authentication module that provides OAuth2 authorization server capabilities with JWT token support, Redis caching, and JPA database integration.

## Technologies
- Spring Boot 3.5.7
- Java 17
- OAuth2 Authorization Server
- Spring Data JPA
- Redis
- Lombok
- Maven

## Features
- OAuth2 Authentication & Authorization
- JWT Token Management
- Redis Session Management
- Database Integration (JPA)
- RESTful API

## Requirements
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL (or your preferred database)
- Redis

## Getting Started

### Running with Docker
```bash
docker-compose up -d
```

### Running Locally
```bash
mvn clean install
mvn spring-boot:run
```

## Configuration
Configure your application in `src/main/resources/application.properties`

## API Endpoints
Coming soon...

## License
MIT
