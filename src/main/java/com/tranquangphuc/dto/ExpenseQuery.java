package com.tranquangphuc.dto;

import java.util.List;

public class ExpenseQuery {
    private Integer year;
    private Integer month;
    private List<String> categories;

    public ExpenseQuery() {
    }

    public ExpenseQuery(Integer year, Integer month, List<String> categories) {
        this.year = year;
        this.month = month;
        this.categories = categories;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "ExpenseQuery{" +
                "year=" + year +
                ", month=" + month +
                ", categories=" + categories +
                '}';
    }
}
