package com.techelevator.tebucks.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;

@Component
public class NewTransferDto {
    private final String SEND = "Send";
    private final String REQUEST = "Request";

    private int userFrom;
    private int userTo;
    private double amount;
    private String transferType;

    @AssertTrue(message = "Cannot send money to yourself.")
    private boolean isUserFromDifferentThanUserTo() {
        return userFrom != userTo;
    }

    @AssertTrue(message = "Cannot send $0 or less.")
    private boolean isAmountToTransferGt0() {
        return amount > 0;
    }

    @AssertTrue(message = "Transfer type must be a send or request.")
    private boolean isTransferTypeSendOrRequest() {
        return Objects.equals(transferType, SEND) || Objects.equals(transferType, REQUEST);
    }

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
