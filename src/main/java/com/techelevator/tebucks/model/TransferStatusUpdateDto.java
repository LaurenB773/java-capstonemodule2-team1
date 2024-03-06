package com.techelevator.tebucks.model;

public class TransferStatusUpdateDto {
    /*
    {
    "transferStatus" : "A string for the transfer status: Pending, Approved, or Rejected"
    }
     */

    private String transferStatus;

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
