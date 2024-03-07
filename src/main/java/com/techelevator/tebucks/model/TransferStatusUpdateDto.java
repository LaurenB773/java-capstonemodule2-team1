package com.techelevator.tebucks.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;

@Component
public class TransferStatusUpdateDto {
    public final String PENDING = "Pending";
    public final String APPROVED = "Approved";
    public final String REJECTED = "Rejected";

    @AssertTrue(message = "Transfer status must be pending, approved, or rejected")
    private boolean isStatusPendingApprovedRejected() {
        return Objects.equals(transferStatus, PENDING) || Objects.equals(transferStatus, APPROVED) || Objects.equals(transferStatus, REJECTED);
    }

    private String transferStatus;

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
