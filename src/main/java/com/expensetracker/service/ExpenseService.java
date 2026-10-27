package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.storage.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/** Business logic for managing expenses. */
public class ExpenseService {

    private final ExpenseRepository repository;
    private final List<Expense> expenses;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
        this.expenses = new ArrayList<>(repository.loadAll());
    }

    public Expense add(BigDecimal amount, String category, String note, LocalDate date) {
        int nextId = expenses.stream().mapToInt(Expense::id).max().orElse(0) + 1;
        Expense expense = new Expense(nextId, date == null ? LocalDate.now() : date, category, amount, note);
        expenses.add(expense);
        repository.saveAll(expenses);
        return expense;
    }

    public List<Expense> list(String category, YearMonth month) {
        return expenses.stream()
                .filter(e -> category == null || e.category().equalsIgnoreCase(category.trim()))
                .filter(e -> month == null || YearMonth.from(e.date()).equals(month))
                .sorted(Comparator.comparing(Expense::date).reversed().thenComparing(Comparator.comparingInt(Expense::id).reversed()))
                .collect(Collectors.toList());
    }

    public boolean delete(int id) {
        boolean removed = expenses.removeIf(e -> e.id() == id);
        if (removed) repository.saveAll(expenses);
        return removed;
    }

    /** Category totals, largest first. */
    public Map<String, BigDecimal> summaryByCategory(YearMonth month) {
        Map<String, BigDecimal> totals = new HashMap<>();
        for (Expense e : list(null, month)) {
            totals.merge(e.category(), e.amount(), BigDecimal::add);
        }
        return totals.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public BigDecimal total(YearMonth month) {
        return list(null, month).stream().map(Expense::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
