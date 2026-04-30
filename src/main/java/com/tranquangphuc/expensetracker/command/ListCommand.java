package com.tranquangphuc.expensetracker.command;

import java.util.List;
import com.tranquangphuc.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "list", description = "List all expenses")
public class ListCommand implements Runnable {

    @Inject
    private ExpenseService service;

    @Option(names = {"-y", "--year"}, description = "Year to filter expenses", required = false)
    private Integer year;

    @Option(names = {"-m", "--month"}, description = "Month to filter expenses", required = false)
    private Integer month;

    @Option(names = {"-c", "--category"}, description = "Filter by one or more categories", required = false)
    private List<String> categories;

    @Override
    public void run() {
        List<Expense> expenses = service.find(new ExpenseQuery(year, month, categories));
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            System.out.println("Expenses:");
            System.out.printf("%9s %10s %6s %s%n", "ID", "Date", "Amount", "Category: Description");
            for (Expense expense : expenses) {
                System.out.printf("%9d %10s %6d %s: %s%n", expense.getId(), expense.getDate(),
                        expense.getAmount(), expense.getCategory(), expense.getDescription());
            }
        }
    }
}
