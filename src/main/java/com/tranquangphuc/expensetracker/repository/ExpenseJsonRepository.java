package com.tranquangphuc.expensetracker.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import com.tranquangphuc.expensetracker.model.Expense;
import jakarta.inject.Inject;
import tools.jackson.databind.ObjectMapper;

public class ExpenseJsonRepository implements ExpenseRepository {

    private static final Path DATA_FILE = Path.of("data.json");

    private ObjectMapper objectMapper;

    private Data data = new Data();

    @Inject
    public ExpenseJsonRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        if (Files.exists(DATA_FILE)) {
            try {
                String json = Files.readString(DATA_FILE);
                data = objectMapper.readValue(json, Data.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to read data file", e);
            }
        } else {
            data.nextId = 1;
            data.expenses = new ArrayList<>();
            save();
        }
    }

    @Override
    public void save() {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            Files.writeString(DATA_FILE, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data file", e);
        }
    }

    @Override
    public List<Expense> find() {
        List<Expense> result = new ArrayList<>(data.expenses);
        result.sort(Comparator.comparing(Expense::getDate));
        return result;
    }

    @Override
    public List<Expense> find(int year) {
        List<Expense> result = data.expenses.stream()
                .filter(it -> it.getDate() != null && it.getDate().getYear() == year)
                .sorted(Comparator.comparing(Expense::getDate)).toList();
        return result;
    }

    @Override
    public List<Expense> find(int year, int month) {
        List<Expense> result = data.expenses.stream()
                .filter(it -> it.getDate() != null && it.getDate().getYear() == year
                        && it.getDate().getMonthValue() == month)
                .sorted(Comparator.comparing(Expense::getDate)).toList();
        return result;
    }

    @Override
    public int add(Expense expense) {
        expense.setId(data.nextId++);
        data.expenses.add(expense);
        save();
        return expense.getId();
    }

    @Override
    public Optional<Expense> delete(int id) {
        Expense toDelete = null;
        Iterator<Expense> iterator = data.expenses.iterator();
        while (iterator.hasNext()) {
            Expense expense = iterator.next();
            if (expense.getId() == id) {
                toDelete = expense;
                iterator.remove();
                save();
                break;
            }
        }
        return Optional.ofNullable(toDelete);
    }

    private static class Data {
        public int nextId;
        public List<Expense> expenses;
    }
}
