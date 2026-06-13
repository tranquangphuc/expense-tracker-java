package com.tranquangphuc.expensetracker.service;

import java.time.LocalDate;
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
