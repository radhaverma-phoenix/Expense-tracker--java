package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Immutable expense entry. */
public record Expense(int id, LocalDate date, String category, BigDecimal amount, String note) {

    public Expense {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category must not be empty");
        }
        category = category.trim().toLowerCase();
        note = note == null ? "" : note.trim();
    }
}
