package com.tranquangphuc.expensetracker.repository;

import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;

public interface ExpenseRepository {
    void save();

    List<Expense> find(ExpenseQuery query);

    Expense add(Expense expense);

    Optional<Expense> delete(int id);
}
