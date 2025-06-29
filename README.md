Here’s your updated and complete `README.md` file with all the requested details **including** the full API flow, tech stack, setup instructions, and optional features—formatted for easy reading and developer use.

---

### ✅ Final `README.md`

```markdown
# 📚 Library Book Borrowing System

A complete Spring Boot application for managing library members, book borrowing, overdue detection, and fine calculation.

---

## 🧠 System Overview

```mermaid
graph TD
    A[📘 Borrow a Book] --> B[📅 Track Borrowings]
    B --> C[⌛ Overdue Detection]
    C --> D[💰 Fine Calculation]
    D --> E[📈 Reporting]
    D --> F[📤 Notifications (Future)]
```

---

## 📘 1. Book Borrowing

### ➕ API: Create a new borrowing

- **URL**: `POST /api/borrowings`

#### Request Body:
```json
{
  "memberId": 1,
  "bookId": 10,
  "borrowDate": "2025-06-25"
}
```

✅ **Result**: Book is borrowed, `dueDate` auto-set to 14 days later.

---

## 📦 2. Get Borrowings

### ✅ Get all borrowings
- `GET /api/borrowings`

### ✅ Get borrowing by ID
- `GET /api/borrowings/{id}`

### ✅ Get all borrowings by member
- `GET /api/borrowings/member/{memberId}`

### ✅ Get active borrowings (not returned yet)
- `GET /api/borrowings/active`

---

## 🔄 3. Return a Book

### ✅ API: Return a borrowed book

- **URL**: `PUT /api/borrowings/{id}/return`

#### Request Body:
```json
{
  "returnDate": "2025-06-30"
}
```

📌 Automatically:
- Updates status to `RETURNED`
- Calculates fine (if overdue)
- Links `Fine` entity (if needed)

---

## ⌛ 4. Overdue Borrowings

### ✅ API: List all overdue borrowings
- `GET /api/borrowings/overdue`

### ➕ Filter by member (optional)
- `GET /api/borrowings/overdue?memberId=1`

---

## 💰 5. Fine Management

### ✅ Get fine by borrowing ID
- `GET /api/fines/borrowing/{borrowingId}`

### ✅ Get all unpaid fines
- `GET /api/fines/unpaid`

### ✅ Mark fine as paid
- `PUT /api/fines/{id}/pay`

---

## 📈 6. Reports

### ✅ Daily Overdue + Fine Summary
- `GET /api/reports/daily-overdue-summary`

### ✅ Member borrowing history
- `GET /api/reports/member/{memberId}/history`

---

## ✅ Optional Features (Future Scope)

| Feature             | Description                              |
|---------------------|------------------------------------------|
| 📆 Scheduled Jobs    | Auto-detect overdue borrowings daily     |
| 📤 Notifications     | SMS/Email reminders for due/overdue books |
| 📊 Admin Dashboard   | Track top borrowers, fines, returns       |

---

## ⚙️ Developer Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-org/library-borrowing-system.git
cd library-borrowing-system
```

### 2. Configure PostgreSQL in `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library_db
    username: postgres
    password: yourpass
```

### 3. Run the application

```bash
./gradlew bootRun
```

### 4. Test APIs

Use **Postman** or **Swagger UI** (if enabled) to test all endpoints.

---

## 👨‍💻 Tech Stack

- 🟨 Java 21
- ⚙️ Spring Boot 3.2
- 🧩 JPA + Hibernate
- 🐘 PostgreSQL
- 🧬 Lombok
- 📄 Swagger (optional, via `springdoc-openapi`)

---

## 🔗 Contribution & Feedback

Feel free to fork, contribute, or raise issues. Your suggestions help us improve.

---
```

---

Let me know if you want:
- This file zipped with your project
- The Swagger configuration added
- The full backend structure including controllers/services/repos for each API listed

Ready to generate! ✅
