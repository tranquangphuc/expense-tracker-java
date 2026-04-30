package com.tranquangphuc.expensetracker.model;

import java.time.LocalDate;

public class Expense {
    private Integer id;
    private String description;
    private Long amount;
    private LocalDate date;
    private String category = "Other";

    public Expense() {}

    public Expense(Integer id, String description, Long amount, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = "Other";
    }

    public Expense(Integer id, String description, Long amount, LocalDate date, String category) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category != null ? category : "Other";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category : "Other";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Expense other = (Expense) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Expense [id=" + id + ", description=" + description + ", amount=" + amount
                + ", date=" + date + ", category=" + category + "]";
    }
}
