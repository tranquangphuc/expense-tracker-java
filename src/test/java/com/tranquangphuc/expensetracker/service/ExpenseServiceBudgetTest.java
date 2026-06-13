package com.tranquangphuc.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.repository.ExpenseJsonRepository;

import tools.jackson.databind.ObjectMapper;

class ExpenseServiceBudgetTest {

    @Test
    void shouldWarnWhenExpensesExceedMonthlyBudget() throws Exception {
        Path tempFile = Files.createTempFile("expense-tracker", ".json");
        ExpenseJsonRepository repository = new ExpenseJsonRepository(new ObjectMapper(), tempFile.toString());

        ExpenseServiceImpl service = new ExpenseServiceImpl();
        service.repository = repository;

        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.setMonthlyBudget(2026, 6, 1000L);

        Optional<String> warning = service.getBudgetWarning(new ExpenseQuery(2026, 6, null));

        assertTrue(warning.isPresent());
        assertTrue(warning.get().contains("exceeds"));
        assertTrue(warning.get().contains("200"));
    }
}
