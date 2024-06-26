package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

public interface AccountDao {
    Account getAccount(int id);
    void updateBalance(int fromUserId, int toUserId, double amount);
}
