# PhonePe Wallet Backend - IntelliJ Setup & Run Instructions

## Prerequisites
- IntelliJ IDEA (Community or Ultimate Edition)
- JDK 17+ (IntelliJ will download automatically if needed)
- MySQL (optional - only if using production profile)

## Quick Start (Development Mode with H2 Database)

### Step 1: Open the Project in IntelliJ
1. Open IntelliJ IDEA
2. Click **File → Open**
3. Navigate to: `c:\Users\yadal\IdeaProjects\Phonepe wallet Backend`
4. Click **Open**
5. When prompted, select **Trust Project** (if you see this dialog)

### Step 2: Configure JDK (if not already configured)
1. Go to **File → Project Structure**
2. Select **Project** in the left sidebar
3. Under **SDK**, click **Edit** or **Add SDK**
4. Click **Download JDK**
5. Select **Eclipse Temurin** version **17** (or higher)
6. Click **Download**
7. Wait for download to complete, then click **OK**

### Step 3: Enable Lombok Annotation Processor
1. Go to **File → Settings** (or **IntelliJ IDEA → Preferences** on macOS)
2. Navigate to **Build, Execution, Deployment → Compiler → Annotation Processors**
3. Check the box: **Enable annotation processing**
4. Click **OK**

### Step 4: Run the Application (Development Mode with H2)
The easiest way is to use the development profile with in-memory H2 database (no MySQL needed):

#### Option A: Using Run Configuration (Recommended)
1. Go to **Run → Edit Configurations**
2. Click **+** (Add New Configuration)
3. Select **Spring Boot**
4. Configure as follows:
   - **Main class**: `com.phonepe.phonepaywallet.PhonepeWalletBackendApplication`
   - **VM options**: (leave empty)
   - **Program arguments**: `--spring.profiles.active=dev`
   - **Working directory**: Your project root
5. Click **OK**
6. Click the green **Run** button (or press **Shift+F10**)

#### Option B: Quick Run
1. Open the main application class:
   [src/main/java/com/phonepe/phonepaywallet/PhonepeWalletBackendApplication.java](src/main/java/com/phonepe/phonepaywallet/PhonepeWalletBackendApplication.java)
2. Click the green **Run** button next to the class name
3. In the Run window, right-click and select **Edit Configuration**
4. Add to **Program arguments**: `--spring.profiles.active=dev`
5. Click **OK** and run again

## How to Access the Application

Once the application starts, you'll see:
```
========================================
PhonePe Wallet Backend Started!
API Base URL: http://localhost:8080/api
Swagger UI: http://localhost:8080/swagger-ui.html
API Docs: http://localhost:8080/v3/api-docs
Database: H2 (in-memory)
========================================
```

### Access Points:
- **Swagger UI (API Documentation)**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:phonepay_db`
  - User: `sa`
  - Password: (leave blank)
- **API Base URL**: http://localhost:8080/api

### Test the API with Swagger UI:
1. Go to: http://localhost:8080/swagger-ui.html
2. You'll see all available endpoints
3. Click on an endpoint to expand it
4. Click **Try it out** button
5. Fill in the request body with test data
6. Click **Execute** to test the endpoint

## Switching Profiles

### Use Development (H2 Database):
- Program arguments: `--spring.profiles.active=dev`
- No external database needed
- Data is in-memory and lost on restart

### Use Production (MySQL Database):
- MySQL must be running with:
  - Database: `phonepay_db`
  - Username: `root`
  - Password: `Chiru@123`
  - Port: `3306`
- No profile argument needed (default uses MySQL)

## Troubleshooting

### Issue: "Cannot find symbol" or "Unresolved reference" errors
**Solution**: Go to **File → Invalidate Caches** and restart IntelliJ

### Issue: "No JDK configured"
**Solution**: Refer to Step 2 above to download and configure JDK 17

### Issue: Lombok annotations not working
**Solution**: 
1. Go to **File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors**
2. Enable **Annotation processing**
3. Invalidate caches and restart

### Issue: Port 8080 already in use
**Solution**: Change the port in `application.properties` or `application-dev.properties`:
```properties
server.port=8081
```

### Issue: H2 Console not accessible
**Solution**: Ensure you're running with the `dev` profile (see "Run the Application" section)

## Common API Endpoints (for testing with Swagger UI)

### User Management:
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login user
- `GET /api/users/profile/{phoneNumber}` - Get user profile

### Wallet Management:
- `GET /api/wallets/{userId}` - Get wallet balance
- `POST /api/wallets/add-money` - Add money to wallet
- `POST /api/wallets/send-money` - Send money to another user

### Transactions:
- `GET /api/transactions/{userId}` - Get transaction history
- `GET /api/transactions/statement/{phoneNumber}` - Get transaction statement

## Next Steps
1. Familiarize yourself with the API using Swagger UI
2. Review the database schema in H2 Console
3. Modify and test the endpoints as needed
4. When ready for production, switch to MySQL database profile

For more information, check the [README.md](README.md) in the project root.
