package com.tranquangphuc.expensetracker.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.service.ExpenseService;

import picocli.CommandLine;

class CommandClassesTest {

    @Test
    void addCommandAddsExpenseAndPrintsSuccess() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        AddCommand command = new AddCommand();
        setService(command, service);

        String output = runCommand(command, "42", "Lunch", "--category", "Food");

        assertEquals(42L, service.addedExpense.getAmount());
        assertEquals("Lunch", service.addedExpense.getDescription());
        assertEquals("Food", service.addedExpense.getCategory());
        assertTrue(output.contains("Expense added successfully"));
    }

    @Test
    void budgetCommandSetsMonthlyBudgetAndPrintsConfirmation() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        BudgetCommand command = new BudgetCommand();
        setService(command, service);

        String output = runCommand(command, "--year", "2026", "--month", "6", "--amount", "1000");

        assertEquals(2026, service.lastYear);
        assertEquals(6, service.lastMonth);
        assertEquals(1000L, service.lastAmount);
        assertTrue(output.contains("Monthly budget set to $1000"));
    }

    @Test
    void deleteCommandDeletesExpenseAndPrintsResult() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        service.deletedExpense = Optional.of(new Expense(7, "Taxi", 200L, LocalDate.of(2026, 6, 10), "Transport"));
        DeleteCommand command = new DeleteCommand();
        setService(command, service);

        String output = runCommand(command, "--id", "7");

        assertEquals(7, service.deletedId);
        assertTrue(output.contains("Expense deleted successfully"));
        assertTrue(output.contains("Taxi"));
    }

    @Test
    void exportCommandExportsExpensesAndPrintsSummary() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        service.expenses = List.of(new Expense(1, "Coffee", 300L, LocalDate.of(2026, 6, 13), "Food"));
        ExportCommand command = new ExportCommand();
        setService(command, service);

        Path outputFile = Path.of("target/test-output/export.csv");
        String output = runCommand(command, "--output", outputFile.toString(), "--year", "2026", "--month", "6");

        assertEquals(outputFile, service.lastExportPath);
        assertEquals(1, service.exportedRows);
        assertTrue(output.contains("Exported 1 expense(s)"));
        assertTrue(output.contains(outputFile.toString()));
    }

    @Test
    void listCommandShowsMatchingExpenses() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        service.expenses = List.of(new Expense(2, "Lunch", 1500L, LocalDate.of(2026, 6, 1), "Food"));
        ListCommand command = new ListCommand();
        setService(command, service);

        String output = runCommand(command, "--year", "2026", "--month", "6", "--category", "Food");

        assertEquals(2026, service.lastQuery.getYear());
        assertEquals(6, service.lastQuery.getMonth());
        assertEquals(List.of("Food"), service.lastQuery.getCategories());
        assertTrue(output.contains("Expenses:"));
        assertTrue(output.contains("Lunch"));
    }

    @Test
    void summaryCommandShowsCategoryTotalsAndBudgetWarning() throws Exception {
        RecordingExpenseService service = new RecordingExpenseService();
        service.expenses = List.of(
                new Expense(3, "Lunch", 1200L, LocalDate.of(2026, 6, 2), "Food"),
                new Expense(4, "Coffee", 300L, LocalDate.of(2026, 6, 3), "Food"));
        service.monthlyBudget = Optional.of(1000L);
        service.warning = Optional.of("Warning: monthly spending exceeds the budget.");
        SummaryCommand command = new SummaryCommand();
        setService(command, service);

        String output = runCommand(command, "--year", "2026", "--month", "6", "--category", "Food");

        assertTrue(output.contains("Expense Summary by Category:"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("TOTAL"));
        assertTrue(output.contains("Monthly budget for 2026-06"));
        assertTrue(output.contains("Warning: monthly spending exceeds the budget."));
    }

    private String runCommand(Object command, String... args) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
        try {
            new CommandLine(command).parseArgs(args);
            ((Runnable) command).run();
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private void setService(Object command, ExpenseService service) throws Exception {
        Field field = command.getClass().getDeclaredField("service");
        field.setAccessible(true);
        field.set(command, service);
    }

    private static class RecordingExpenseService implements ExpenseService {
        private Expense addedExpense;
        private ExpenseQuery lastQuery;
        private int deletedId;
        private Optional<Expense> deletedExpense = Optional.empty();
        private List<Expense> expenses = List.of();
        private int exportedRows;
        private Path lastExportPath;
        private int lastYear;
        private int lastMonth;
        private long lastAmount;
        private Optional<Long> monthlyBudget = Optional.empty();
        private Optional<String> warning = Optional.empty();

        @Override
        public Expense add(Expense expense) {
            this.addedExpense = expense;
            return expense;
        }

        @Override
        public List<Expense> find(ExpenseQuery query) {
            this.lastQuery = query;
            return expenses;
        }

        @Override
        public Optional<Expense> delete(int id) {
            this.deletedId = id;
            return deletedExpense;
        }

        @Override
        public long summary(ExpenseQuery query) {
            return expenses.stream().mapToLong(Expense::getAmount).sum();
        }

        @Override
        public int exportToCsv(Path file, ExpenseQuery query) throws IOException {
            this.lastExportPath = file;
            this.lastQuery = query;
            this.exportedRows = expenses.size();
            return exportedRows;
        }

        @Override
        public void setMonthlyBudget(int year, int month, long amount) {
            this.lastYear = year;
            this.lastMonth = month;
            this.lastAmount = amount;
        }

        @Override
        public Optional<Long> getMonthlyBudget(int year, int month) {
            return monthlyBudget;
        }

        @Override
        public Optional<String> getBudgetWarning(ExpenseQuery query) {
            return warning;
        }
    }
}
