package com.tranquangphuc.expensetracker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.repository.ExpenseRepository;
import jakarta.inject.Inject;

public class ExpenseServiceImpl implements ExpenseService {

    @Inject
    ExpenseRepository repository;

    @Override
    public Expense add(Expense expense) {
        Objects.requireNonNull(expense);
        Objects.requireNonNull(expense.getDescription());
        Objects.requireNonNull(expense.getAmount());
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        if (expense.getCategory() == null) {
            expense.setCategory("Other");
        }
        return repository.add(expense);
    }

    @Override
    public List<Expense> find(ExpenseQuery query) {
        return repository.find(query);
    }

    @Override
    public Optional<Expense> delete(int id) {
        return repository.delete(id);
    }

    @Override
    public long summary(ExpenseQuery query) {
        List<Expense> expenses = find(query);
        return expenses.stream().mapToLong(Expense::getAmount).sum();
    }

    @Override
    public int exportToCsv(Path file, ExpenseQuery query) throws IOException {
        Objects.requireNonNull(file, "CSV file path must not be null");

        Path resolvedPath = file.toAbsolutePath().normalize();
        Path parent = resolvedPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<Expense> expenses = find(query);
        StringBuilder csv = new StringBuilder();
        csv.append("id,date,description,amount,category").append(System.lineSeparator());
        for (Expense expense : expenses) {
            csv.append(escapeCsv(String.valueOf(expense.getId()))).append(',')
                    .append(escapeCsv(expense.getDate() == null ? "" : expense.getDate().toString())).append(',')
                    .append(escapeCsv(expense.getDescription())).append(',')
                    .append(escapeCsv(String.valueOf(expense.getAmount()))).append(',')
                    .append(escapeCsv(expense.getCategory())).append(System.lineSeparator());
        }

        Files.writeString(resolvedPath, csv);
        return expenses.size();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }

    @Override
    public void setMonthlyBudget(int year, int month, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Budget amount must be non-negative");
        }
        repository.setMonthlyBudget(year, month, amount);
    }

    @Override
    public Optional<Long> getMonthlyBudget(int year, int month) {
        return repository.getMonthlyBudget(year, month);
    }

    @Override
    public Optional<String> getBudgetWarning(ExpenseQuery query) {
        if (query == null) {
            query = new ExpenseQuery();
        }

        int resolvedYear = query.getYear() != null ? query.getYear() : LocalDate.now().getYear();
        int resolvedMonth = query.getMonth() != null ? query.getMonth() : LocalDate.now().getMonthValue();
        ExpenseQuery resolvedQuery = new ExpenseQuery(resolvedYear, resolvedMonth, query.getCategories());

        Optional<Long> budget = getMonthlyBudget(resolvedYear, resolvedMonth);
        if (budget.isEmpty()) {
            return Optional.empty();
        }

        long spent = summary(resolvedQuery);
        long overage = spent - budget.get();
        if (overage > 0) {
            return Optional.of(String.format(
                    "Warning: monthly spending of $%d exceeds the budget of $%d by $%d.",
                    spent, budget.get(), overage));
        }

        return Optional.empty();
    }
}
