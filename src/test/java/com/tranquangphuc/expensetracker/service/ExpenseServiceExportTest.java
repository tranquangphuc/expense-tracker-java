package com.tranquangphuc.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.repository.ExpenseJsonRepository;
import com.tranquangphuc.expensetracker.repository.ExpenseRepository;

import tools.jackson.databind.ObjectMapper;

class ExpenseServiceExportTest {

    @Test
    void shouldExportMatchingExpensesToCsv() throws Exception {
        Path tempDataFile = Files.createTempFile("expense-tracker", ".json");
        Path tempCsvFile = Files.createTempFile("expense-tracker", ".csv");
        Files.deleteIfExists(tempCsvFile);

        ExpenseJsonRepository repository = new ExpenseJsonRepository(new ObjectMapper(), tempDataFile.toString());

        ExpenseServiceImpl service = new ExpenseServiceImpl();
        service.repository = repository;

        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.add(new Expense(null, "Train ticket", 3000L, LocalDate.of(2025, 1, 5), "Travel"));

        int exportedRows = service.exportToCsv(tempCsvFile, new ExpenseQuery(2026, 6, List.of("Food")));

        assertEquals(1, exportedRows);
        assertTrue(Files.exists(tempCsvFile));

        String content = Files.readString(tempCsvFile);
        assertTrue(content.startsWith("id,date,description,amount,category"));
        assertTrue(content.contains("Lunch"));
        assertTrue(content.contains("Food"));
        assertFalse(content.contains("Train ticket"));
    }

    @Test
    void shouldExportExpensesOrderedByDateAscending() throws Exception {
        Path tempCsvFile = Files.createTempFile("expense-tracker", ".csv");
        Files.deleteIfExists(tempCsvFile);

        ExpenseServiceImpl service = new ExpenseServiceImpl();
        service.repository = new StubExpenseRepository(List.of(
                new Expense(2, "Later expense", 4000L, LocalDate.of(2026, 6, 20), "Other"),
                new Expense(1, "Earlier expense", 2000L, LocalDate.of(2026, 6, 1), "Food")));

        service.exportToCsv(tempCsvFile, new ExpenseQuery());

        String content = Files.readString(tempCsvFile);
        String[] rows = content.split(System.lineSeparator());
        assertTrue(rows[1].contains("Earlier expense"));
        assertTrue(rows[2].contains("Later expense"));
        assertTrue(content.indexOf("Earlier expense") < content.indexOf("Later expense"));
    }

    private static class StubExpenseRepository implements ExpenseRepository {
        private final List<Expense> expenses;

        private StubExpenseRepository(List<Expense> expenses) {
            this.expenses = new ArrayList<>(expenses);
        }

        @Override
        public void save() {
        }

        @Override
        public List<Expense> find(ExpenseQuery query) {
            return expenses.stream()
                    .sorted(Comparator.comparing(Expense::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(Expense::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
        }

        @Override
        public Expense add(Expense expense) {
            return expense;
        }

        @Override
        public Optional<Expense> delete(int id) {
            return Optional.empty();
        }

        @Override
        public void setMonthlyBudget(int year, int month, long amount) {
        }

        @Override
        public Optional<Long> getMonthlyBudget(int year, int month) {
            return Optional.empty();
        }
    }
}
