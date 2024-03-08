package com.techelevator.tebucks.TEARS;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TearsLogDto {
    private String description;
    @JsonProperty("username_from")
    private String username_from;
    @JsonProperty("username_to")
    private String username_to;
    private double amount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername_from() {
        return username_from;
    }

    public void setUsername_from(String username_from) {
        this.username_from = username_from;
    }

    public String getUsername_to() {
        return username_to;
    }

    public void setUsername_to(String username_to) {
        this.username_to = username_to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
