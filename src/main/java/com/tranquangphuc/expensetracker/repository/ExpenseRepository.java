package com.tranquangphuc.expensetracker.repository;

import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.model.Expense;

public interface ExpenseRepository {
    void save();

    List<Expense> find();

    List<Expense> find(int year);

    List<Expense> find(int year, int month);

    int add(Expense expense);

    Optional<Expense> delete(int id);
}
