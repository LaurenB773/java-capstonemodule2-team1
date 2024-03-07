package com.techelevator.tebucks.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

public class Transfer {
    @NotBlank
    private int transferId;
    @NotBlank
    private int userFromId;
    @NotBlank
    private int userToId;
    private Double amountToTransfer;
    private boolean isSuccessful;

    @AssertTrue(message = "Cannot send money to yourself.")
    private boolean isUserFromDifferentThanUserTo() {
        return userFromId != userToId;
    }
    @AssertTrue(message = "Cannot send $0 or less.")
    private boolean isAmountToTransferGt0() {
        return amountToTransfer > 0;
    }

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

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }
}
