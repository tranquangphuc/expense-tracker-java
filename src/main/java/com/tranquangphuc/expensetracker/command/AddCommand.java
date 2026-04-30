package com.tranquangphuc.expensetracker.command;

import java.time.LocalDate;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "add", description = "Add a new expense")
public class AddCommand implements Runnable {
    @Inject
    ExpenseService service;

    @Parameters(index = "0", description = "Amount of the expense", arity = "1", paramLabel = "<amount>")
    private Long amount;

    @Parameters(index = "1", description = "Description of the expense", arity = "1", paramLabel = "<description>")
    private String description;

    @Option(names = {"-c", "--category"}, description = "Category of the expense (default: Other)", required = false)
    private String category;

    @Option(names = {"-d", "--date"}, description = "Date of the expense in YYYY-MM-DD format", required = false)
    private LocalDate date;

    @Override
    public void run() {
        Expense expense = service.add(new Expense(null, description, amount, date, category));
        System.out.println("Expense added successfully (ID: " + expense.getId() + ")");
    }
}
