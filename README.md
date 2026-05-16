<<<<<<< HEAD
# Bank-Management-System-Java
=======
# 🏦 JavaBank – Bank Management System

A console-based Bank Management System built with **Java** and **MySQL (JDBC)**.

---

## 📌 Features

### Customer
- Register & Login
- Open Savings / Current account
- Deposit, Withdraw, Transfer funds
- View mini statement (last 5 transactions)
- View full account statement
- Check balance

### Admin
- View all customers
- View all accounts
- View all transactions
- Close an account

---

## 🛠️ Technologies Used

| Tech | Purpose |
|------|---------|
| Java (JDK 8+) | Core programming language |
| MySQL | Relational database |
| JDBC | Java-MySQL connectivity |
| OOP | Encapsulation, Abstraction via DAO/Service layers |
| Collections | ArrayList for data handling |
| Exception Handling | Try-catch for DB errors |

---

## 📁 Project Structure

```
BankManagementSystem/
├── src/
│   └── com/bank/
│       ├── Main.java               ← Entry point (console UI)
│       ├── model/
│       │   ├── Account.java
│       │   ├── Customer.java
│       │   └── Transaction.java
│       ├── dao/
│       │   ├── AccountDAO.java
│       │   ├── CustomerDAO.java
│       │   ├── TransactionDAO.java
│       │   └── UserDAO.java
│       ├── service/
│       │   └── BankService.java
│       └── util/
│           └── DBConnection.java
└── database/
    └── schema.sql
```

---

## ⚙️ Setup Instructions

### Step 1 – Prerequisites
- Java JDK 8 or above
- MySQL Server installed
- MySQL Connector/J JAR (download from https://dev.mysql.com/downloads/connector/j/)

### Step 2 – Database Setup
1. Open MySQL Workbench or terminal
2. Run the SQL script:
```sql
source /path/to/BankManagementSystem/database/schema.sql
```

### Step 3 – Configure Database Connection
Open `src/com/bank/util/DBConnection.java` and update:
```java
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

### Step 4 – Compile & Run

**Using terminal:**
```bash
# Add MySQL connector to classpath
javac -cp .;mysql-connector-j-8.x.xx.jar src/com/bank/**/*.java src/com/bank/*.java -d out/

java -cp out;mysql-connector-j-8.x.xx.jar com.bank.Main
```

**Using IntelliJ IDEA:**
1. Open the project folder
2. Add MySQL Connector JAR: `File → Project Structure → Libraries → +`
3. Mark `src` as Sources Root
4. Run `Main.java`

---

## 🔐 Default Admin Login
```
Username: admin
Password: admin123
```

---

## 📝 Resume Description (copy-paste ready)

**Bank Management System** | Java, MySQL, JDBC | [GitHub Link]
- Developed a console-based banking application in Java with MySQL database integration using JDBC
- Implemented role-based access (Admin/Customer) with secure login and registration
- Features include account management, deposits, withdrawals, fund transfers, and transaction history
- Applied OOP principles (encapsulation, abstraction) using a layered DAO-Service architecture
- Handled SQL exceptions and edge cases such as insufficient funds and duplicate usernames

---

## 💡 Java Concepts Covered (for interviews)
- **OOP**: Classes, Objects, Encapsulation, Abstraction
- **Collections**: ArrayList, List interface
- **Exception Handling**: try-catch-finally blocks
- **JDBC**: PreparedStatement, ResultSet, Connection
- **Design Pattern**: Singleton (DBConnection), DAO pattern
>>>>>>> 258262f (Initial commit: Bank Management System using Java + MYSQL JDBC)
