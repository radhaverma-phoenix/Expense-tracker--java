package com.expensetracker.storage;

import com.expensetracker.model.Expense;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Stores expenses in a plain CSV file (id,date,category,amount,note). */
public class ExpenseRepository {

    private static final String HEADER = "id,date,category,amount,note";
    private final Path file;

    public ExpenseRepository(Path file) {
        this.file = file;
    }

    public List<Expense> loadAll() {
        List<Expense> result = new ArrayList<>();
        if (!Files.exists(file)) {
            return result;
        }
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) { // skip header
                if (lines.get(i).isBlank()) continue;
                List<String> f = parseLine(lines.get(i));
                result.add(new Expense(
                        Integer.parseInt(f.get(0)),
                        LocalDate.parse(f.get(1)),
                        f.get(2),
                        new BigDecimal(f.get(3)),
                        f.size() > 4 ? f.get(4) : ""));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
        return result;
    }

    public void saveAll(List<Expense> expenses) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Expense e : expenses) {
            lines.add(e.id() + "," + e.date() + "," + escape(e.category()) + ","
                    + e.amount().toPlainString() + "," + escape(e.note()));
        }
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(file, lines);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write " + file, e);
        }
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /** Minimal CSV parser supporting quoted fields. */
    static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else if (c == '"') {
                    quoted = false;
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields;
    }
}
