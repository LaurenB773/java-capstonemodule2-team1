package com.techelevator.tebucks.model;

import javax.validation.constraints.Min;

public class Account {
    public static final Double STARTING_BALANCE = 1000.00;
    private int accountId;
    private int userId;
    @Min(0)
    private Double balance;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
