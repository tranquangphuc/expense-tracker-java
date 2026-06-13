package com.tranquangphuc.expensetracker.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "export", description = "Export expenses to a CSV file")
public class ExportCommand implements Runnable {

    @Inject
    private ExpenseService service;

    @Option(names = {"-o", "--output"}, description = "Output CSV file path", required = true)
    private Path output;

    @Option(names = {"-y", "--year"}, description = "Year to filter expenses", required = false)
    private Integer year;

    @Option(names = {"-m", "--month"}, description = "Month to filter expenses", required = false)
    private Integer month;

    @Option(names = {"-c", "--category"}, description = "Filter by one or more categories", required = false)
    private List<String> categories;

    @Override
    public void run() {
        try {
            int exportedRows = service.exportToCsv(output, new ExpenseQuery(year, month, categories));
            System.out.printf("Exported %d expense(s) to %s%n", exportedRows, output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export expenses to CSV", e);
        }
    }
}
