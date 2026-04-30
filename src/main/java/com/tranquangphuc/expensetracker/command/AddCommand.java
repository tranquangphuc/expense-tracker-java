package com.tranquangphuc.expensetracker.command;

import java.time.LocalDate;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "add", description = "Add a new expense")
public class AddCommand implements Runnable {
    @Inject
    ExpenseService service;

    @Option(names = {"-d", "--description"}, description = "Description of the expense", required = true)
    private String description;

    @Option(names = {"-a", "--amount"}, description = "Amount of the expense", required = true)
    private Long amount;

    @Option(names = {"-c", "--category"}, description = "Category of the expense (default: Other)", required = false)
    private String category;

    @Option(names = {"--date"}, description = "Date of the expense in YYYY-MM-DD format", required = false)
    private LocalDate date;

    @Override
    public void run() {
        Expense expense = service.add(new Expense(null, description, amount, date, category));
        System.out.println("Expense added successfully (ID: " + expense.getId() + ")");
    }
}
