# PhonePe Wallet Backend - Edits Summary

## All Changes Made to Run Application in IntelliJ

### 1. **Updated pom.xml** 
   - **File**: `pom.xml`
   - **Change**: Added H2 database dependency for development
   - **New Dependency Added**:
   ```xml
   <!-- H2 Database (for development) -->
   <dependency>
       <groupId>com.h2database</groupId>
       <artifactId>h2</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
   - **Reason**: Allows application to run without MySQL being installed/running. H2 provides an in-memory database for development and testing.

### 2. **Created application-dev.properties**
   - **File**: `src/main/resources/application-dev.properties` (NEW FILE)
   - **Configuration**: H2 in-memory database setup
   - **Key Settings**:
     - Database: `jdbc:h2:mem:phonepay_db` (in-memory)
     - H2 Console enabled at `http://localhost:8080/h2-console`
     - Hibernate DDL: `create-drop` (auto-creates schema)
     - Username: `sa` (default H2 user)
   - **Reason**: Development profile that requires no external database setup

### 3. **Updated application.properties**
   - **File**: `src/main/resources/application.properties`
   - **Changes**: 
     - Added instructions for using dev profile
     - Kept MySQL configuration as default (production)
   - **Instructions Added**:
   ```properties
   # NOTE: To run in IntelliJ without MySQL installed, use the 'dev' profile with H2 database
   # Program Arguments: --spring.profiles.active=dev
   ```
   - **Reason**: Users can choose between dev (H2) and production (MySQL) profiles

### 4. **Created INTELLIJ_SETUP.md**
   - **File**: `INTELLIJ_SETUP.md` (NEW FILE)
   - **Contents**: Complete setup and run instructions including:
     - Step-by-step project setup in IntelliJ
     - JDK configuration guide
     - Lombok annotation processor setup
     - Multiple ways to run the application
     - API access instructions
     - H2 Console access information
     - Common troubleshooting guide
   - **Reason**: Provides clear guidance for running the application without JDK/MySQL issues

## No Code Changes Required

The following were verified and are working correctly:
- ✅ All Java source files (no syntax errors)
- ✅ All entity classes with proper JPA annotations
- ✅ All repositories with required query methods
- ✅ All services with business logic
- ✅ All controllers with REST endpoints
- ✅ All DTOs with proper validation annotations
- ✅ Swagger/OpenAPI configuration
- ✅ Lombok annotations properly configured

## How to Run in IntelliJ Now

### Quick Start:
1. Open the project in IntelliJ
2. Configure JDK 17 (if not already done)
3. Enable Lombok annotation processor
4. Create a run configuration with Program Arguments: `--spring.profiles.active=dev`
5. Click Run

### Access Points:
- **API Documentation (Swagger UI)**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
- **API Base URL**: http://localhost:8080/api

## Database Setup

### Development (H2 - In-Memory):
- No setup required
- Fresh database on each restart with test data
- Perfect for development and testing

### Production (MySQL):
- Database: `phonepay_db`
- Username: `root`
- Password: `Chiru@123`
- Port: `3306`
- Run with default profile (no `--spring.profiles.active` argument)

## Project Structure Verified

```
✅ Controllers: UserController, WalletController, TransactionController
✅ Services: UserService, WalletService, TransactionService
✅ Entities: User, Wallet, Transaction
✅ Repositories: UserRepository, WalletRepository, TransactionRepository
✅ DTOs: All 7 request/response classes with validation
✅ Config: SwaggerConfig for API documentation
✅ Properties: application.properties + application-dev.properties
```

## All API Endpoints Available

### User Management:
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - Login user
- `GET /api/users/profile/{phoneNumber}` - Get user profile

### Wallet Management:
- `GET /api/wallet/{upiId}` - Get wallet balance
- `POST /api/wallet/add` - Add money to wallet

### Transactions:
- `POST /api/transaction/send` - Send money
- `GET /api/transaction/history/{upiId}` - Get transaction history

## Testing with Swagger UI

1. Navigate to: http://localhost:8080/swagger-ui.html
2. All endpoints are documented and testable directly from the UI
3. Click "Try it out" on any endpoint to test it
4. View database via H2 Console at: http://localhost:8080/h2-console

## Notes

- **H2 Console Credentials**:
  - JDBC URL: `jdbc:h2:mem:phonepay_db`
  - User: `sa`
  - Password: (leave blank)

- **Data Persistence**: In dev mode, all data is in-memory and reset on restart

- **Lombok**: Make sure annotation processor is enabled in IntelliJ settings

- **Port Conflict**: If port 8080 is busy, update `server.port` in the properties file

---

**All edits are complete. The application is ready to run in IntelliJ!**
