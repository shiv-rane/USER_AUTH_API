
# 🔐 USER AUTHENTICATION API

An OTP-based authentication API built using Spring Boot. After login via email and password, the user receives an OTP on email. On successful OTP verification, the server issues a JWT (RS256) as an HttpOnly cookie.

---

## 🚀 Features

- ✅ Email/password login with **BCrypt hashing**
- ✅ OTP generation and verification (via email)
- ✅ **JWT token issuance** (RS256, HttpOnly cookie)
- ✅ Stateless JWT authentication with **Spring Security**
- ✅ **Rate limiting** with Redis
- ✅ RSA key-based signing
- ✅ OTP auto-expiry with scheduled cleanup
- ✅ Global exception handling
- ✅ Clean modular architecture (DTO, service, repository layers)

---

## 💻 Tech Stack

| Layer             | Stack                              |
| ----------------- | ---------------------------------- |
| Backend           | Spring Boot + Spring Security      |
| Database          | MySQL                              |
| Password Hashing  | BCrypt                             |
| Token Management  | JWT (RS256), httpOnly Cookies      |
| Rate Limiting     | Redis                              |
| Email Service     | Java Mail (SMTP)                   |

---

## ⚙️ Auth Flow

1. **Login** with email/password → validate via `BCrypt`
2. **Send OTP** to user’s email (after password validation)
3. **Verify OTP** → if valid, issue **JWT (RS256)** as an HttpOnly cookie
4. Authenticated routes are protected via **JWT filter**

---

## 🚦 Rate Limiting

- **Max OTP Requests**: 3 per user
- **Max OTP Verifications**: 5 per user
- Implemented using **Redis**

---

## 🔐 JWT Security

- Uses RS256 (asymmetric)
- Keys stored in:
  ```
  src/main/resources/keys/
  ├── private.pem
  └── public.pem
  ```
- Issued token is stored as **HttpOnly cookie** for extra security

---

## 📮 Endpoints

| Method | Endpoint         | Description                      |
|--------|------------------|----------------------------------|
| POST   | `/register`      | User enters personal credentials |
| POST   | `/login`         | Login with email & password      |
| POST   | `/verify-otp`    | Verify OTP and return JWT cookie |
| GET    | `/me       `     | Sample protected route           |

---

## 📄 License

MIT
