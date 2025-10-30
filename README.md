# Authentication Module

A comprehensive Spring Boot authentication and authorization system with JWT, email verification, and password reset functionality.

## Features

- 🔐 JWT-based authentication (Access & Refresh tokens)
- 👤 User registration with email verification
- 🔑 Password reset functionality
- 📝 Audit logging for security events
- 🐳 Docker containerization
- 📊 Swagger/OpenAPI documentation
- 🗄️ PostgreSQL database
- ⚡ Redis caching
- 🛡️ Spring Security integration

## Tech Stack

- **Framework**: Spring Boot 3.5.7
- **Java**: 17
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)
- Maven 3.9+ (for local development)

## Quick Start with Docker

### 1. Clone the repository

```bash
git clone https://github.com/CCM-EV/AuthModule.git
cd AuthModule
```

### 2. Run with Docker Compose

```bash
docker-compose up -d
```

This command will:
- Start PostgreSQL database on port 5432
- Start Redis on port 6379
- Build and start the Spring Boot application on port 8080

### 3. Check application status

```bash
docker-compose ps
```

### 4. View logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app
```

### 5. Stop the application

```bash
docker-compose down
```

To remove volumes as well:
```bash
docker-compose down -v
```

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Local Development

### 1. Start PostgreSQL and Redis

```bash
docker-compose up -d postgres redis
```

### 2. Run the application

```bash
mvn spring-boot:run
```

Or run from your IDE.

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database (update for your environment)
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT (change secret in production!)
jwt.secret=your-secret-key
jwt.access-token-expiration=900000        # 15 minutes
jwt.refresh-token-expiration=604800000    # 7 days
```

## Environment Variables

For Docker deployment, you can override these environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://postgres:5432/authdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `SPRING_DATA_REDIS_HOST` | Redis host | `redis` |
| `SPRING_DATA_REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT signing secret | (see compose file) |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token lifetime (ms) | `900000` |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token lifetime (ms) | `604800000` |

## Project Structure

```
authmodule/
├── src/
│   ├── main/
│   │   ├── java/com/xdpmhdt/authmodule/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Building

### Build JAR

```bash
mvn clean package
```

### Build Docker Image

```bash
docker build -t authmodule:latest .
```

## Testing

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0.

## Contact

**XDPMHDT**
- Email: contact@xdpmhdt.com
- GitHub: [@CCM-EV](https://github.com/CCM-EV)
