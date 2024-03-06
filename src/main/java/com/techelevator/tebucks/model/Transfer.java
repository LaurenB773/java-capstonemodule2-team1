package com.techelevator.tebucks.model;

public class Transfer {
    private int transferId;
    private int userFromId;
    private int userToId;
    private Double amountToTransfer;
    private boolean isSucessful;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(int userFromId) {
        this.userFromId = userFromId;
    }

    public int getUserToId() {
        return userToId;
    }

    public void setUserToId(int userToId) {
        this.userToId = userToId;
    }

    public Double getAmountToTransfer() {
        return amountToTransfer;
    }

    public void setAmountToTransfer(Double amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }

    public boolean isSucessful() {
        return isSucessful;
    }

    public void setSucessful(boolean sucessful) {
        isSucessful = sucessful;
    }
}
