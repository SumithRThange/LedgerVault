# LedgerVault (Time-Bound Wallet System)

Beginner-friendly full project with:
- `backend/`: Spring Boot + MySQL (single service)
- `android-app/`: Android app (Java + XML + Retrofit)

No microservices, Kafka, JWT, or complex security.

## 1) Backend (Spring Boot)

### Tech Stack
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Bean Validation
- Scheduling (`@EnableScheduling`)

### Backend Structure
```
backend/src/main/java/com/timeboundwallet/
  controller/
  service/
  repository/
  entity/
  dto/
  exception/
  config/
```

### Database Config
Configured in `backend/src/main/resources/application.yml`:
- DB: `time_bound_wallet_db`
- username: `root`
- password: `root`
- port: `8080`

Update credentials if your MySQL setup is different.

### Run Backend
1. Start MySQL server.
2. From `backend/` run:
   ```bash
   mvn spring-boot:run
   ```
3. Backend starts on:
   `http://localhost:8080`

### Main APIs
- `POST /users` -> Create user
- `GET /users` -> List users
- `GET /users/{id}` -> Get user by ID
- `PUT /users/{id}` -> Update own credentials (`X-USER-ID` header)
- `PUT /users/{id}/deactivate` -> Deactivate own account (`X-USER-ID` header)
- `DELETE /users/{id}` -> Delete own account (`X-USER-ID` header)
- `POST /wallet` -> Create wallet for user
- `GET /wallet/{userId}/balance` -> Get wallet balance
- `POST /wallet/{userId}/add` -> Add money
- `POST /wallet/transfer` -> Transfer between wallets
- `GET /wallet/{walletId}/transactions` -> Transaction history

### Scheduler
Daily expiry job in `WalletService#processExpiredCredits`:
- Finds ACTIVE CREDIT transactions with `expiryDate < today`
- Marks original transaction as EXPIRED
- Deducts amount from wallet balance (if enough balance)
- Creates REFUND transaction with status REFUNDED

## 2) Android App (Java)

### Tech Stack
- Java
- XML layouts
- Retrofit + Gson
- RecyclerView

### Android Structure
```
android-app/app/src/main/java/com/timeboundwallet/
  activities/
  adapter/
  network/
  models/
```

### Screens
- Register Screen (`RegisterActivity`)
- Dashboard (`DashboardActivity`)
- Add Money (`AddMoneyActivity`)
- Transfer (`TransferActivity`)
- Transaction History (`TransactionHistoryActivity`)

### Base URL (Retrofit)
In `android-app/app/src/main/java/com/timeboundwallet/network/RetrofitClient.java`:
- Render URL: `https://ledgervault-79sw.onrender.com/`
- Health check: `GET https://ledgervault-79sw.onrender.com/users`

### Run Android App
1. Open `android-app/` in Android Studio.
2. Let Gradle sync.
3. Run app on emulator/device.
4. If using local backend, update `RetrofitClient` with your local host/IP.

## 3) Sample JSON Request Bodies

### Create User
`POST /users`
```json
{
  "name": "Alice",
  "email": "alice@example.com"
}
```

### Create Wallet
`POST /wallet`
```json
{
  "userId": 1,
  "maxLimit": 10000
}
```

### Add Money
`POST /wallet/1/add`
```json
{
  "amount": 500
}
```

### Transfer
`POST /wallet/transfer`
```json
{
  "senderWalletId": 1,
  "receiverWalletId": 2,
  "amount": 200
}
```

## 4) Simple Test Data Flow
1. Create user A
2. Create wallet for user A
3. Create user B
4. Create wallet for user B
5. Add money to user A wallet
6. Transfer amount from wallet A to wallet B
7. Check transactions for both wallets
