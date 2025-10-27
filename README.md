# Card Vault

A secure Spring Boot application for storing and managing credit card information with encryption. The application provides both web interface and REST API endpoints for card management operations.

## Features

- Secure storage of credit card information
- AES encryption for PAN (Primary Account Number)
- PAN validation
- Duplicate PAN detection
- Search cards by last 4 digits
- Web interface for card management
- REST API endpoints
- Unit tests with JUnit and Mockito

## Tech Stack

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- JUnit 5
- Mockito
- HTML/CSS/JavaScript
- MySQL Database

## Prerequisites

- JDK 17 or later
- Maven 3.8+
- MySQL 8.0 or later

## Configuration

The application requires the following configuration in `application.properties`:

```properties
# Encryption Configuration
creditcard.encryption.key=your-32-char-key
creditcard.encryption.algorithm=AES

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Logging Configuration (Optional)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type=TRACE
```

### Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE mydatabase;
```

2. Update the database configuration in `application.properties` with your MySQL credentials.

3. The application will automatically create the required tables on startup due to `spring.jpa.hibernate.ddl-auto=update`.

## Building the Application

```bash
cd card-vault
mvn clean install
```

## Running the Application

```bash
cd card-vault
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Endpoints

### Web Interface

- `GET /` - Home page
- `GET /addCard` - Add new card page
- `POST /save` - Save new card
- `GET /search` - Search cards page
- `POST /searchByPan` - Search cards by PAN

### REST API

- `POST /creditcards/` - Add new credit card
- `GET /creditcards/{panLastFourDigits}` - Find cards by last 4 digits

## Security Features

- PAN encryption using AES
- Masked PAN display
- Hashed PAN last 4 digits for searching
- Input validation
- Exception handling for invalid inputs

## Testing

```bash
mvn test
```

## License

[MIT License](LICENSE)
