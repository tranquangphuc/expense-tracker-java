package com.tranquangphuc.expensetracker.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.tranquangphuc.expensetracker.dto.ExpenseQuery;
import com.tranquangphuc.expensetracker.model.Expense;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tools.jackson.databind.ObjectMapper;

public class ExpenseJsonRepository implements ExpenseRepository {

    private final Path dataFile;

    private final ObjectMapper objectMapper;

    private JsonData data = new JsonData();

    @Inject
    public ExpenseJsonRepository(ObjectMapper objectMapper, @Named("data.file") String dataFilePath) {
        this.objectMapper = objectMapper;
        this.dataFile = Path.of(dataFilePath);
        if (Files.exists(this.dataFile)) {
            try {
                String json = Files.readString(this.dataFile);
                if (json == null || json.isBlank()) {
                    initializeEmptyData();
                } else {
                    data = objectMapper.readValue(json, JsonData.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to read data file", e);
            }
        } else {
            initializeEmptyData();
            save();
        }

        if (data.expenses == null) {
            data.expenses = new ArrayList<>();
        }
        if (data.monthlyBudgets == null) {
            data.monthlyBudgets = new LinkedHashMap<>();
        }
    }

    private void initializeEmptyData() {
        data.nextId = 1;
        data.expenses = new ArrayList<>();
        data.monthlyBudgets = new LinkedHashMap<>();
    }

    @Override
    public void save() {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            Files.writeString(dataFile, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data file", e);
        }
    }

    @Override
    public List<Expense> find(ExpenseQuery query) {
        if (query == null) {
            query = new ExpenseQuery();
        }

        Integer year = query.getYear();
        Integer month = query.getMonth();
        List<String> categories = query.getCategories();

        List<Expense> result = data.expenses.stream()
                .filter(expense -> matchesAnyCategory(expense, categories))
                .filter(expense -> year == null || (expense.getDate() != null && expense.getDate().getYear() == year))
                .filter(expense -> month == null || (expense.getDate() != null && expense.getDate().getMonthValue() == month))
                .sorted(Comparator.comparing(Expense::getDate).thenComparing(Expense::getId)).toList();
        return result;
    }

    private boolean matchesAnyCategory(Expense expense, List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }
        String expenseCategory = expense.getCategory();
        if (expenseCategory == null) {
            return false;
        }
        for (String category : categories) {
            if (expenseCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Expense add(Expense expense) {
        expense.setId(data.nextId++);
        data.expenses.add(expense);
        save();
        return expense;
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

    @Override
    public void setMonthlyBudget(int year, int month, long amount) {
        data.monthlyBudgets.put(toBudgetKey(year, month), amount);
        save();
    }

    @Override
    public Optional<Long> getMonthlyBudget(int year, int month) {
        return Optional.ofNullable(data.monthlyBudgets.get(toBudgetKey(year, month)));
    }

    private String toBudgetKey(int year, int month) {
        return String.format("%04d-%02d", year, month);
    }

    private static class JsonData {
        public int nextId;
        public List<Expense> expenses;
        public Map<String, Long> monthlyBudgets = new LinkedHashMap<>();
    }
}
