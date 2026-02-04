package com.tranquangphuc.expensetracker.command;

import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "summary", description = "Show summary of expenses")
public class SummaryCommand implements Runnable {

    @Inject
    private ExpenseService service;

    @Option(names = {"-y", "--year"}, description = "Year for summary", required = false)
    private Integer year;

    @Option(names = {"-m", "--month"}, description = "Month for summary", required = false)
    private Integer month;

    @Override
    public void run() {
        long total = service.summary(year, month);
        System.out.println("Total expenses: $" + total);
    }

}
