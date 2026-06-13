package com.tranquangphuc.expensetracker.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;

public interface ExpenseService {
    Expense add(Expense expense);

    List<Expense> find(ExpenseQuery query);

    Optional<Expense> delete(int id);

    long summary(ExpenseQuery query);

    int exportToCsv(Path file, ExpenseQuery query) throws IOException;

    void setMonthlyBudget(int year, int month, long amount);

    Optional<Long> getMonthlyBudget(int year, int month);

    Optional<String> getBudgetWarning(ExpenseQuery query);
}
