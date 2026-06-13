package com.tranquangphuc.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.repository.ExpenseJsonRepository;

import tools.jackson.databind.ObjectMapper;

class ExpenseServiceImplTest {

    @Test
    void shouldAddExpenseAndPopulateDefaults() throws Exception {
        ExpenseServiceImpl service = newService();

        Expense saved = service.add(new Expense(null, "Coffee", 450L, null, null));

        assertNotNull(saved.getId());
        assertEquals("Coffee", saved.getDescription());
        assertEquals(450L, saved.getAmount());
        assertNotNull(saved.getDate());
        assertEquals("Other", saved.getCategory());
        assertEquals(1, service.find(new ExpenseQuery()).size());
    }

    @Test
    void shouldFindExpensesMatchingQuery() throws Exception {
        ExpenseServiceImpl service = newService();
        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.add(new Expense(null, "Train", 3000L, LocalDate.of(2026, 6, 5), "Travel"));

        List<Expense> found = service.find(new ExpenseQuery(2026, 6, List.of("Food")));

        assertEquals(1, found.size());
        assertEquals("Lunch", found.get(0).getDescription());
    }

    @Test
    void shouldDeleteExpenseAndReturnIt() throws Exception {
        ExpenseServiceImpl service = newService();
        Expense saved = service.add(new Expense(null, "Taxi", 800L, LocalDate.of(2026, 6, 3), "Travel"));

        Optional<Expense> deleted = service.delete(saved.getId());

        assertTrue(deleted.isPresent());
        assertEquals(saved.getId(), deleted.get().getId());
        assertTrue(service.find(new ExpenseQuery()).isEmpty());
    }

    @Test
    void shouldSummarizeMatchingExpenses() throws Exception {
        ExpenseServiceImpl service = newService();
        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.add(new Expense(null, "Dinner", 1800L, LocalDate.of(2026, 6, 2), "Food"));
        service.add(new Expense(null, "Train", 3000L, LocalDate.of(2026, 6, 3), "Travel"));

        long total = service.summary(new ExpenseQuery(2026, 6, List.of("Food")));

        assertEquals(3000L, total);
    }

    @Test
    void shouldExportMatchingExpensesToCsv() throws Exception {
        ExpenseServiceImpl service = newService();
        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.add(new Expense(null, "Train", 3000L, LocalDate.of(2026, 6, 2), "Travel"));

        Path csvFile = Files.createTempFile("expense-service", ".csv");
        Files.deleteIfExists(csvFile);

        int exportedRows = service.exportToCsv(csvFile, new ExpenseQuery(2026, 6, List.of("Food")));

        assertEquals(1, exportedRows);
        assertTrue(Files.exists(csvFile));
        String content = Files.readString(csvFile);
        assertTrue(content.contains("Lunch"));
        assertFalse(content.contains("Train"));
    }

    @Test
    void shouldSetAndGetMonthlyBudget() throws Exception {
        ExpenseServiceImpl service = newService();

        service.setMonthlyBudget(2026, 6, 2500L);

        Optional<Long> budget = service.getMonthlyBudget(2026, 6);
        assertTrue(budget.isPresent());
        assertEquals(2500L, budget.get());
    }

    @Test
    void shouldWarnWhenBudgetExceeded() throws Exception {
        ExpenseServiceImpl service = newService();
        service.add(new Expense(null, "Lunch", 1200L, LocalDate.of(2026, 6, 1), "Food"));
        service.setMonthlyBudget(2026, 6, 1000L);

        Optional<String> warning = service.getBudgetWarning(new ExpenseQuery(2026, 6, null));

        assertTrue(warning.isPresent());
        assertTrue(warning.get().contains("exceeds"));
    }

    @Test
    void shouldRejectNegativeMonthlyBudget() throws Exception {
        ExpenseServiceImpl service = newService();

        assertThrows(IllegalArgumentException.class, () -> service.setMonthlyBudget(2026, 6, -1L));
    }

    private ExpenseServiceImpl newService() throws Exception {
        Path tempDataFile = Files.createTempFile("expense-service", ".json");
        ExpenseJsonRepository repository = new ExpenseJsonRepository(new ObjectMapper(), tempDataFile.toString());

        ExpenseServiceImpl service = new ExpenseServiceImpl();
        service.repository = repository;
        return service;
    }
}
