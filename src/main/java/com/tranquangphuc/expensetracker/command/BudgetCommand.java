package com.tranquangphuc.expensetracker.command;

import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "budget", description = "Set a monthly budget")
public class BudgetCommand implements Runnable {

    @Inject
    private ExpenseService service;

    @Option(names = {"-y", "--year"}, description = "Year for the budget", required = true)
    private int year;

    @Option(names = {"-m", "--month"}, description = "Month for the budget", required = true)
    private int month;

    @Option(names = {"-a", "--amount"}, description = "Budget amount", required = true)
    private long amount;

    @Override
    public void run() {
        service.setMonthlyBudget(year, month, amount);
        System.out.printf("Monthly budget set to $%d for %04d-%02d%n", amount, year, month);
    }
}
