package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

public interface AccountDao {
    Account getAccountBalance(int id);
    void updateBalanceSend(int fromUserId, int toUserId, double amount);
    void updateBalanceRequest(int fromUserId, int toUserId, double amount);
}
