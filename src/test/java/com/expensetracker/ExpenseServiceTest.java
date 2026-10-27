package com.expensetracker;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.storage.ExpenseRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

/** Dependency-free tests. Run with: java -cp out com.expensetracker.ExpenseServiceTest */
public class ExpenseServiceTest {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        testAddAndList();
        testFilters();
        testDelete();
        testSummary();
        testPersistenceWithCommasInNote();
        testInvalidAmount();
        if (failures > 0) {
            System.out.println(failures + " test(s) FAILED");
            System.exit(1);
        }
        System.out.println("All tests passed.");
    }

    private static ExpenseService newService() throws Exception {
        Path tmp = Files.createTempDirectory("exp").resolve("e.csv");
        return new ExpenseService(new ExpenseRepository(tmp));
    }

    private static void check(boolean condition, String name) {
        System.out.println((condition ? "PASS " : "FAIL ") + name);
        if (!condition) failures++;
    }

    static void testAddAndList() throws Exception {
        ExpenseService s = newService();
        s.add(new BigDecimal("250"), "Food", "lunch", LocalDate.of(2026, 10, 1));
        check(s.list(null, null).size() == 1 && s.list(null, null).get(0).category().equals("food"), "add and list");
    }

    static void testFilters() throws Exception {
        ExpenseService s = newService();
        s.add(new BigDecimal("100"), "food", "", LocalDate.of(2026, 10, 5));
        s.add(new BigDecimal("200"), "travel", "", LocalDate.of(2026, 9, 5));
        check(s.list(null, YearMonth.of(2026, 10)).size() == 1, "filter by month");
        check(s.list("TRAVEL", null).size() == 1, "filter by category (case-insensitive)");
    }

    static void testDelete() throws Exception {
        ExpenseService s = newService();
        Expense e = s.add(new BigDecimal("50"), "misc", "", null);
        check(s.delete(e.id()) && !s.delete(e.id()), "delete");
    }

    static void testSummary() throws Exception {
        ExpenseService s = newService();
        s.add(new BigDecimal("100"), "food", "", LocalDate.of(2026, 10, 1));
        s.add(new BigDecimal("150"), "food", "", LocalDate.of(2026, 10, 2));
        s.add(new BigDecimal("40"), "books", "", LocalDate.of(2026, 10, 3));
        Map<String, BigDecimal> sum = s.summaryByCategory(null);
        check(sum.get("food").compareTo(new BigDecimal("250")) == 0
                && sum.keySet().iterator().next().equals("food")
                && s.total(null).compareTo(new BigDecimal("290")) == 0, "summary and total");
    }

    static void testPersistenceWithCommasInNote() throws Exception {
        Path tmp = Files.createTempDirectory("exp").resolve("e.csv");
        ExpenseService a = new ExpenseService(new ExpenseRepository(tmp));
        a.add(new BigDecimal("75.50"), "food", "tea, snacks \"special\"", LocalDate.of(2026, 10, 7));
        ExpenseService b = new ExpenseService(new ExpenseRepository(tmp)); // reload from disk
        Expense e = b.list(null, null).get(0);
        check(e.note().equals("tea, snacks \"special\"") && e.amount().compareTo(new BigDecimal("75.50")) == 0,
                "persistence with commas/quotes");
    }

    static void testInvalidAmount() throws Exception {
        ExpenseService s = newService();
        boolean threw = false;
        try { s.add(new BigDecimal("-5"), "food", "", null); } catch (IllegalArgumentException ex) { threw = true; }
        check(threw, "rejects non-positive amount");
    }
}
