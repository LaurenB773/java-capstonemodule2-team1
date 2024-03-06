package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<Transfer> getAccountTransfers(int accountId);

    Transfer createTransfer();

    Transfer updateTransfer();

}
