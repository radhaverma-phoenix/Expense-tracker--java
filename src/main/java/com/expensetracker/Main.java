package com.expensetracker;

import com.expensetracker.service.ExpenseService;
import com.expensetracker.storage.ExpenseRepository;
import com.expensetracker.ui.ConsoleUI;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path dataFile = Path.of(args.length > 0 ? args[0] : "data/expenses.csv");
        ExpenseService service = new ExpenseService(new ExpenseRepository(dataFile));
        new ConsoleUI(service).run();
    }
}
