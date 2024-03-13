package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

public interface AccountDao {
    Account getAccountByUserId(int userFromId);
    void updateBalance(int fromUserId, int toUserId, double amount);
}
