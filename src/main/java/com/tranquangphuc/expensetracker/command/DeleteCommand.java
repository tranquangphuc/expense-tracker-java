package com.tranquangphuc.expensetracker.command;

import java.util.Optional;
import com.tranquangphuc.expensetracker.model.Expense;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "delete", description = "Delete an expense by ID")
public class DeleteCommand implements Runnable {

    @Inject
    private ExpenseService service;

    @Option(names = {"-i", "--id"}, description = "ID of the expense to delete", required = true)
    private Integer id;

    @Override
    public void run() {
        Optional<Expense> deleted = service.delete(id);
        if (deleted.isPresent()) {
            System.out.println("Expense deleted successfully: " + deleted.get());
        } else {
            System.out.println("Expense with ID " + id + " not found");
        }
    }

}
