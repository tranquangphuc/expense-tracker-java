package com.tranquangphuc.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.tranquangphuc.dto.ExpenseQuery;
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

}
