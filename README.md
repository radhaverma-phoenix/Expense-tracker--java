# Expense Tracker (Java)

A console-based expense tracker written in plain Java 17+. No external libraries needed. Data is stored in a simple CSV file.

## Features
- Add expenses (amount, category, note, date)
- View expenses, filtered by category and/or month
- Delete expenses by id
- Category-wise summary with totals
- Data saved automatically to `data/expenses.csv`
- Dependency-free unit tests

## Project Structure
```
expense-tracker-java/
├── src/
│   ├── main/java/com/expensetracker/
│   │   ├── Main.java                      # entry point
│   │   ├── model/Expense.java             # data model (record)
│   │   ├── storage/ExpenseRepository.java # CSV persistence
│   │   ├── service/ExpenseService.java    # business logic
│   │   └── ui/ConsoleUI.java              # interactive menu
│   └── test/java/com/expensetracker/
│       └── ExpenseServiceTest.java
├── .gitignore
├── LICENSE
└── README.md
```

## Build and Run
Requires JDK 17 or newer.

```bash
git clone https://github.com/<your-username>/expense-tracker-java.git
cd expense-tracker-java

# compile
mkdir out
javac -d out $(find src/main -name "*.java")

# run (optional argument: path to data file)
java -cp out com.expensetracker.Main
```
On Windows PowerShell, compile with:
```powershell
javac -d out (Get-ChildItem -Recurse src/main -Filter *.java).FullName
```

## Run Tests
```bash
javac -d out $(find src -name "*.java")
java -cp out com.expensetracker.ExpenseServiceTest
```

## Future Improvements
- Migrate storage to SQLite/MySQL via JDBC
- Monthly budget limits and alerts
- JavaFX or Spring Boot web interface
- Maven/Gradle build with JUnit

## License
MIT
