package com.techelevator.tebucks.model;

public class NewTransferDto {
    /*
    {
    "userFrom" : "An integer holding the id for the user that is transfering the money",
    "userTo" : "An integer holding the id for the user that is receiving the money",
    "amount" : "A decimal indicating the amount to transfer",
    "transferType" : "A string for the transfer type: Send or Request",
     }
     */

    private int userFrom;
    private int userTo;
    private Double amount;
    private String transferType;

    public int getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(int userFrom) {
        this.userFrom = userFrom;
    }

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
