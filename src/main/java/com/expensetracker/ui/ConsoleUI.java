package com.expensetracker.ui;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Interactive text menu. */
public class ConsoleUI {

    private final ExpenseService service;
    private final Scanner in = new Scanner(System.in);

    public ConsoleUI(ExpenseService service) {
        this.service = service;
    }

    public void run() {
        System.out.println("=== Expense Tracker ===");
        while (true) {
            System.out.println("\n1. Add expense\n2. View expenses\n3. Delete expense\n4. Summary\n5. Exit");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addExpense();
                    case "2" -> viewExpenses();
                    case "3" -> deleteExpense();
                    case "4" -> showSummary();
                    case "5" -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addExpense() {
        BigDecimal amount = new BigDecimal(ask("Amount: "));
        String category = ask("Category: ");
        String note = ask("Note (optional): ");
        String d = ask("Date YYYY-MM-DD (blank = today): ");
        LocalDate date = d.isBlank() ? null : LocalDate.parse(d);
        Expense e = service.add(amount, category, note, date);
        System.out.println("Added expense #" + e.id());
    }

    private void viewExpenses() {
        String cat = ask("Filter by category (blank = all): ");
        String m = ask("Filter by month YYYY-MM (blank = all): ");
        List<Expense> list = service.list(cat.isBlank() ? null : cat, m.isBlank() ? null : YearMonth.parse(m));
        if (list.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        System.out.printf("%-5s %-12s %-14s %12s  %s%n", "ID", "Date", "Category", "Amount", "Note");
        for (Expense e : list) {
            System.out.printf("%-5d %-12s %-14s %12.2f  %s%n", e.id(), e.date(), e.category(), e.amount(), e.note());
        }
    }

    private void deleteExpense() {
        int id = Integer.parseInt(ask("Expense id: "));
        System.out.println(service.delete(id) ? "Deleted." : "No expense with that id.");
    }

    private void showSummary() {
        String m = ask("Month YYYY-MM (blank = all time): ");
        YearMonth month = m.isBlank() ? null : YearMonth.parse(m);
        for (Map.Entry<String, BigDecimal> e : service.summaryByCategory(month).entrySet()) {
            System.out.printf("%-14s %12.2f%n", e.getKey(), e.getValue());
        }
        System.out.println("-".repeat(27));
        System.out.printf("%-14s %12.2f%n", "TOTAL", service.total(month));
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }
}
