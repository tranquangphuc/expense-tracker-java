package com.tranquangphuc.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.repository.ExpenseRepository;
import jakarta.inject.Inject;

public class ExpenseServiceImpl implements ExpenseService {

    @Inject
    ExpenseRepository repository;

    @Override
    public int add(String description, Long amount) {
        Expense expense = new Expense(null, description, amount, LocalDate.now());
        return repository.add(expense);
    }

    @Override
    public int add(String description, Long amount, LocalDate date) {
        Expense expense = new Expense(null, description, amount, date);
        return repository.add(expense);
    }

    @Override
    public List<Expense> find(Integer year, Integer month) {
        if (year != null && month != null) {
            return repository.find(year, month);
        } else if (year != null) {
            return repository.find(year);
        } else if (month != null) {
            year = LocalDate.now().getYear();
            return repository.find(year, month);
        }

        return repository.find();
    }

    @Override
    public Optional<Expense> delete(int id) {
        return repository.delete(id);
    }

    @Override
    public long summary(Integer year, Integer month) {
        List<Expense> expenses = find(year, month);
        return expenses.stream().mapToLong(Expense::getAmount).sum();
    }

}
