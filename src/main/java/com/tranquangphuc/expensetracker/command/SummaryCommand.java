package com.tranquangphuc.expensetracker.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
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

    @Option(names = {"-c", "--category"}, description = "Filter by one or more categories", required = false)
    private List<String> categories;

    @Override
    public void run() {
        // Show per-category breakdown
        List<Expense> expenses = service.find(new ExpenseQuery(year, month, categories));
        Map<String, List<Expense>> expensesByCategory =
                expenses.stream().collect(Collectors.groupingBy(Expense::getCategory));

        System.out.println("Expense Summary by Category:");
        System.out.printf("%-15s %8s %8s%n", "Category", "Count", "Total");
        System.out.println("--------------------------------");

        long grandTotal = 0;
        for (Map.Entry<String, List<Expense>> entry : expensesByCategory.entrySet()) {
            String category = entry.getKey();
            List<Expense> categoryExpenses = entry.getValue();
            int count = categoryExpenses.size();
            long total = categoryExpenses.stream().mapToLong(Expense::getAmount).sum();
            grandTotal += total;
            System.out.printf("%-15s %8d $%7d%n", category, count, total);
        }
        System.out.println("--------------------------------");
        System.out.printf("%-15s %8s $%7d%n", "TOTAL", "", grandTotal);
    }

}
