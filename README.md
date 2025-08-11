# MBB Assignment

A Spring Boot application that process transaction file.

## Technology Stack

- **Backend**: Spring Boot 3.5.4, Spring Data JPA, Spring Validation
- **Database**: MySQL 8, H2 for testing
- **Cache/Lock**: Redis 8 for distributed locking
- **Build Tool**: Maven 3.9
- **Java**: JDK 21
- **Migration**: Flyway for database schema management

## Getting Started

### Prerequisites
- Java 21 or higher
- Docker and Docker Compose
- Maven 3.6+

### Starting Docker Services

Start MySQL and Redis containers:
```bash
docker-compose up -d
```

### Starting the Application

Clean build and start the application:
```bash
mvn clean && mvn
```

Alternative start command:
```bash
mvn clean spring-boot:run
```

The application will start on `http://localhost:8080` with:
- Automatic Flyway migrations
- Transaction file processing (5 seconds after startup)

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Classes
```bash
# Run repository tests
mvn test -Dtest=*RepositoryTests

# Run specific test class
mvn test -Dtest=TransactionControllerTests
```

### Test Profiles
Tests automatically use H2 in-memory database and test profile:
```bash
mvn test -Dspring.profiles.active=test
```

## Build

### Build JAR File
```bash
mvn clean package
```

### Build Without Tests
```bash
mvn clean package -DskipTests
```

### Run JAR Directly
```bash
java -jar target/assignment-*.jar
```

### Batch Processing
The system automatically processes transaction files from `src/main/resources/data/`:
- **Schedule**:
    - 5 seconds after startup (just to make data available to test the endpoints)
    - daily at midnight
- **Batch Size**: Configurable (default: 100 records)

## Notes

### Flyway Database Migration

The application uses Flyway for database schema management:

- **Migration Location**: `src/main/resources/db/migration/`
- **Auto-Migration**: Enabled by default on startup

### Redis Distributed Locking

Implements pessimistic locking for concurrent transaction updates:

**Configuration**:
- **Host**: localhost:6379
- **Timeout**: 2000ms connection timeout
- **Lock Duration**: 20 seconds automatic expiration
- **Retry Logic**: 10 attempts with 500ms intervals

**Lock Key Pattern**: `transaction:{transactionId}`
**Lock Value**: `{threadName}:{timestamp}` for ownership verification

### Concurrency Flow

```
Client Request → Acquire Redis Lock → Update Database → Release Lock → Return Response
                      ↓ (if lock fails)
                 Retry Logic (10x) → Success / ResourceLockedException
```

## API Endpoints

#### GET http://localhost:8080/api/v1/transactions
Search transactions with pagination and filtering.

**Query Parameters**:
- `customerId` (Long, optional): Filter by customer ID
- `accountNo` (String, optional): Filter by account number  
- `description` (String, optional): Filter by description (partial match, case-insensitive)
- `page` (int, optional, default: 0): Page number
- `size` (int, optional, default: 20): Page size
- `sort` (String, optional, default: "date,desc"): Sort criteria

#### PUT http://localhost:8080/api/v1/transactions/{id}
Update transaction description with concurrent update protection.

**Path Parameters**:
- `id` (String, required): Transaction ID

**Request Body**:
```json
{
  "description": "Updated transaction description"
}
```