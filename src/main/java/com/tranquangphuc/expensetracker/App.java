package com.tranquangphuc.expensetracker;

import com.tranquangphuc.expensetracker.command.AddCommand;
import com.tranquangphuc.expensetracker.command.BudgetCommand;
import com.tranquangphuc.expensetracker.command.DeleteCommand;
import com.tranquangphuc.expensetracker.command.ExportCommand;
import com.tranquangphuc.expensetracker.command.ListCommand;
import com.tranquangphuc.expensetracker.command.SummaryCommand;
import com.tranquangphuc.expensetracker.config.GuiceFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "expense-tracker", mixinStandardHelpOptions = true, version = "expense-tracker 0.1",
        description = "A simple expense tracker application.", subcommands = {AddCommand.class,
                BudgetCommand.class, DeleteCommand.class, ExportCommand.class, ListCommand.class, SummaryCommand.class})
public class App {
    public static void main(String[] args) {
        int exitCode = new CommandLine(App.class, new GuiceFactory()).execute(args);
        System.exit(exitCode);
    }
}
