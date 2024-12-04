# Bank JDBC Application

## Description

The **Bank JDBC Application** is a console-based banking system implemented in Java. It provides a simulation of basic banking operations such as account creation, balance inquiry, fixed deposits, money transfers, and more. The application uses JDBC (Java Database Connectivity) for database interactions and file handling for passbook records.

---

## Features

1. **User Registration and Login**
   - User signs up by providing details like name, age, mobile number, and SSN.
   - The system validates user inputs for correctness.
   - A 4-digit PIN is set during account creation, which is used for login.

2. **Banking Operations**
   - **Check Balance**: View the current account balance.
   - **Fixed Deposit**: Calculate future returns based on the deposit amount and duration.
   - **Money Transfer**: Transfer money to another account.
   - **Request Handling**: Includes requests for bank statements, credit cards, or updating account details.

3. **Passbook Generation**
   - Each transaction (deposits, withdrawals, transfers) is recorded in a passbook file.
   - The passbook can be viewed in a formatted text file.

4. **Database Integration**
   - Stores account details, balances, and transaction history in a database using JDBC.

5. **Input Validation**
   - Ensures valid data for mobile numbers, PINs, SSNs, and sufficient balances for operations.

---

## Technologies Used

- **Java**: Core programming language.
- **JDBC**: For database connectivity.
- **File Handling**: To generate and update passbook files.
- **SQL Database**: For storing account and transaction data.

---

## Setup Instructions

1. **Database Setup**
   - Create a database named `bank`.
   - Add two tables:
     - **`bank`**: To store account details (`Account_no`, `Balance`, etc.).
     - **`transactions`**: To log transactions (`account_id`, `transaction_type`, `amount`, etc.).
   - Ensure your database driver is configured properly in your system.

2. **Code Configuration**
   - Update the JDBC URL, username, and password in the code:
     ```java
     Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "username", "password");
     ```

3. **Compilation and Execution**
   - Compile the program:
     ```bash
     javac Bankjdbc.java
     ```
   - Run the program:
     ```bash
     java Bankjdbc
     ```

4. **Passbook File**
   - A passbook file will be created with the name `<Account_no>.txt` in the working directory.

---

## Usage Instructions

1. **Registration**
   - Provide your name, age, mobile number, and SSN.
   - Set a 4-digit PIN and an initial deposit amount.

2. **Login**
   - Use your account number and PIN to log in.

3. **Perform Operations**
   - Choose from the menu:
     - View balance.
     - Make a fixed deposit.
     - Transfer money.
     - Request bank services.
     - View your passbook.

4. **Exit**
   - Safely log out and exit the application.

---

## Example Menu Workflow

1. User signs up with valid details.
2. Logs in using their account number and PIN.
3. Chooses a service like viewing the balance or transferring money.
4. Transactions are recorded in the database and the passbook.

---

## Notes

- Ensure the database is running and accessible before starting the application.
- Only valid data will be accepted; invalid entries prompt re-input.
- Fixed deposit interest rate is 8% annually.

---

## Future Enhancements

- Add support for multiple users.
- Implement advanced security measures like encryption for PINs.
- Create a graphical user interface for ease of use.
- Enable email notifications for transactions.

---

## Author

Developed by Ridham Patel a passionate Java developer to simulate real-world banking operations.
