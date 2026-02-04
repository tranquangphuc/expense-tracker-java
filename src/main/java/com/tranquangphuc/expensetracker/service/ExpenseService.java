package com.tranquangphuc.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.model.Expense;

public interface ExpenseService {
    int add(String description, Long amount);

    int add(String description, Long amount, LocalDate date);

    List<Expense> find(Integer year, Integer month);

    Optional<Expense> delete(int id);

    long summary(Integer year, Integer month);
}
