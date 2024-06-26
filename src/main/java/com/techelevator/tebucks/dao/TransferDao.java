package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;

import java.util.List;

public interface TransferDao {
    List<Transfer> getAccountTransfers(int accountId);

    Transfer getTransferById(int transferId);

    Transfer createTransfer(NewTransferDto transfer);

    Transfer updateTransfer(TransferStatusUpdateDto transferStatusUpdateDto, int transferId);

}
