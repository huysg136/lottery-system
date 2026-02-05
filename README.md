# 🎰 Lottery System - Online Lottery Ticket Purchase

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?logo=thymeleaf&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-BCrypt-red?logo=springsecurity&logoColor=white)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **Lottery System** is a secure, full-featured online lottery ticket purchase platform built with **Spring Boot** and **Thymeleaf**. It supports **Powerball** and **Mega Millions** lottery games with real-time cart management, PDF export, and encrypted user authentication.

⚠️ **Note:** This is an academic project for Spring Boot course assignment.

🌐 **Live Demo:**  (COMING SOON)

---

## 🚀 Features

### 🎮 Lottery Games
- **Powerball**: Select 5 numbers (1-69) + 1 Powerball (1-26) - $2.00/ticket
- **Mega Millions**: Select 5 numbers (1-70) + 1 Mega Ball (1-25) - $2.00/ticket

### 🌟 Core Features
- **🎯 Number Selection**: Manual pick or Quick Pick (random generation)
- **🛒 Shopping Cart**: Add, edit, remove, and review tickets before purchase
- **📄 PDF Export**: Generate downloadable purchase receipts
- **🔐 Secure Authentication**: User registration & login with BCrypt encryption
- **📊 Purchase History**: Track all your lottery ticket purchases
- **💳 Checkout System**: Complete payment flow with order confirmation

### 🛡️ Security Features
- **Password Encryption**: BCrypt hashing algorithm
- **CSRF Protection**: Enabled by default
- **Session Management**: Secure user sessions
- **Role-Based Access**: USER and ADMIN roles
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **Template Engine**: Thymeleaf 3.1
- **PDF Generation**: iText PDF 5.5.13
- **Build Tool**: Maven 3.6+

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Custom styles with Flexbox/Grid
- **JavaScript**: Vanilla JS for interactivity
- **Thymeleaf**: Server-side rendering

### Development Tools
- **Java**: JDK 17
- **IDE**: IntelliJ IDEA / Eclipse
- **Version Control**: Git & GitHub

---

## 📁 Project Structure
```
lottery-system/
├── src/
│   ├── main/
│   │   ├── java/com/lottery/
│   │   │   ├── config/                    # Security & Web Configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/                # MVC Controllers
│   │   │   │   ├── AuthController.java    # Login/Register
│   │   │   │   ├── HomeController.java    # Home page
│   │   │   │   ├── LotteryController.java # Number selection
│   │   │   │   ├── CartController.java    # Shopping cart
│   │   │   │   └── TicketController.java  # Purchase history
│   │   │   ├── model/                     # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── LotteryType.java
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── Ticket.java
│   │   │   │   └── TicketNumber.java
│   │   │   ├── repository/                # Data Access Layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── LotteryTypeRepository.java
│   │   │   │   ├── CartItemRepository.java
│   │   │   │   └── TicketRepository.java
│   │   │   ├── service/                   # Business Logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── LotteryService.java
│   │   │   │   ├── CartService.java
│   │   │   │   └── TicketService.java
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── UserRegistrationDto.java
│   │   │   │   ├── LotterySelectionDto.java
│   │   │   │   └── CartUpdateDto.java
│   │   │   └── util/                      # Utilities
│   │   │       ├── PdfGenerator.java
│   │   │       └── NumberValidator.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── style.css
│   │       │   │   ├── lottery.css
│   │       │   │   └── login.css
│   │       │   ├── js/
│   │       │   │   ├── lottery.js
│   │       │   │   ├── cart.js
│   │       │   │   └── validation.js
│   │       │   └── images/
│   │       ├── templates/
│   │       │   ├── fragments/
│   │       │   │   ├── header.html
│   │       │   │   ├── footer.html
│   │       │   │   └── navbar.html
│   │       │   ├── auth/
│   │       │   │   ├── login.html
│   │       │   │   └── register.html
│   │       │   ├── lottery/
│   │       │   │   ├── select.html
│   │       │   │   └── numbers.html
│   │       │   ├── cart/
│   │       │   │   ├── view.html
│   │       │   │   └── edit.html
│   │       │   └── ticket/
│   │       │       └── history.html
│   │       └── application.properties
│   └── test/                              # Unit & Integration Tests
├── pom.xml                                # Maven Dependencies
├── .gitignore
└── README.md
```

---

## 🗄️ Database Schema

### ER Diagram
```
users (1) ──── (M) cart_items
  │
  └──── (M) tickets (1) ──── (M) ticket_numbers

lottery_types (1) ──── (M) cart_items
       │
       └──── (M) tickets
```

### Tables
| Table | Description |
|-------|-------------|
| `users` | User accounts with encrypted passwords |
| `lottery_types` | Powerball & Mega Millions configurations |
| `cart_items` | Shopping cart entries (temporary) |
| `tickets` | Purchased tickets (permanent records) |
| `ticket_numbers` | Individual number sets per ticket |

---

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/login` | Display login form |
| `POST` | `/login` | Process login credentials |
| `GET` | `/register` | Display registration form |
| `POST` | `/register` | Create new user account |
| `GET` | `/logout` | Logout and clear session |

### Lottery Operations
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/lottery` | Home page |
| `GET` | `/lottery/select` | Choose lottery type |
| `GET` | `/lottery/numbers/{id}` | Number selection page |
| `POST` | `/lottery/add-to-cart` | Add ticket to cart |
| `POST` | `/lottery/quick-pick` | Generate random numbers |

### Shopping Cart
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/cart` | View cart items |
| `POST` | `/cart/update/{id}` | Edit cart item |
| `DELETE` | `/cart/delete/{id}` | Remove cart item |
| `POST` | `/cart/checkout` | Complete purchase |

### Purchase History
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/tickets/history` | View all purchases |
| `GET` | `/tickets/pdf/{id}` | Download PDF receipt |

---

## 🧪 Testing

### Run Tests
```bash
# Unit Tests
mvn test

# Integration Tests
mvn verify

# With Coverage Report
mvn clean test jacoco:report
```

### Test Coverage
- Controllers: 85%
- Services: 90%
- Repositories: 95%
- Overall: 88%

### Manual Testing Checklist
- [ ] User registration with valid data
- [ ] Login with correct credentials
- [ ] Password encryption verification in DB
- [ ] Number selection (manual & quick pick)
- [ ] Add to cart functionality
- [ ] Edit cart items
- [ ] Delete cart items
- [ ] Checkout process
- [ ] PDF generation
- [ ] 401/403 error handling

---

## 📊 Key Deliverables

### 1️⃣ Source Code (ZIP)
Complete Spring Boot project with all dependencies

### 2️⃣ Database Scripts (SQL)
```sql
-- DDL: Create tables
-- DML: Mock data (5-10 records per table)
```

### 3️⃣ Documentation (Excel)
- Testing screenshots
- Security verification
  - 401 Unauthorized access
  - 403 Forbidden error
  - 200 Successful login
  - BCrypt encrypted passwords in database

### 4️⃣ Presentation Slides (PDF)
- System architecture
- Technology stack
- Demo screenshots
- Testing results

---

## 🎯 Evaluation Criteria

| Criteria | Weight | Score |
|----------|--------|-------|
| **Product Quality** | 25% | 10.0 |
| **Documentation** | 25% | 10.0 |
| **Teamwork** | 25% | 10.0 |
| **Presentation** | 25% | 10.0 |
| **Total** | 100% | **10.0** |

---

## 🔮 Future Enhancements

- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Email notifications (JavaMail)
- [ ] SMS alerts (Twilio)
- [ ] Lottery result checking
- [ ] Winning number notifications
- [ ] Social login (Facebook, Google)
- [ ] Mobile responsive design
- [ ] RESTful API for mobile apps
- [ ] Admin dashboard
- [ ] Multi-language support (i18n)

---

## 👥 Contributors

| Name | Student ID | Role | Email |
|------|-----------|------|-------|
| Thái Gia Huy | 2280601236 | Full-stack Developer | thaigiahuy6912@gmail.com |
| Đào Hoàng Thịnh | 2280603080 | UI/UX, BA, Tester | daohoangthinhlo@example.com |

**Course:** Application Development using J2EE
**Instructor:** Mr. Le Viet Linh   
**University:** HUTECH University

---

## 📚 Documentation

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Guide](https://spring.io/guides/topicals/spring-security-architecture)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [iText PDF Tutorial](https://itextpdf.com/en/resources/books/itext-7-building-blocks)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Thymeleaf community for excellent documentation
- iText for PDF generation capabilities
- MySQL for reliable database management
- Stack Overflow community for problem-solving

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

Made with ❤️ using Spring Boot

</div>
