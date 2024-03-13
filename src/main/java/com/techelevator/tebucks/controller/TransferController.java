package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {
    private TransferDao transferDao;
    private AccountDao accountDao;
    private UserDao userDao;
    private NewTransferDto newTransferDto;
    private TransferStatusUpdateDto transferStatusUpdateDto;

    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao,
                              NewTransferDto newTransferDto, TransferStatusUpdateDto transferStatusUpdateDto) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.newTransferDto = newTransferDto;
        this.transferStatusUpdateDto = transferStatusUpdateDto;
    }

    @GetMapping("/transfers/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        Transfer transfer = transferDao.getTransferById(id);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        } else {
            return transfer;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/transfers")
    public Transfer createTransfer(@Valid @RequestBody NewTransferDto newTransfer) {
        Transfer createdTransfer = transferDao.createTransfer(newTransfer);
        User fromUser = createdTransfer.getUserFrom();
        int fromUserId = fromUser.getId();
        User toUser = createdTransfer.getUserTo();
        int toUserId = toUser.getId();
        double amount = createdTransfer.getAmount();

        Account account = accountDao.getAccountByUserId(fromUserId);

        if (createdTransfer.getTransferType().equals("Send") && amount > account.getBalance()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot sent more money than in balance");
        }

        if (createdTransfer.getTransferType().equals("Send") && amount > 0
                && amount < account.getBalance()) {
            accountDao.updateBalance(fromUserId, toUserId, amount);
        }

        return createdTransfer;
    }

    @PutMapping("/transfers/{id}/status")
    public Transfer updateTransfer(@Valid @RequestBody TransferStatusUpdateDto transferStatus,
                                   @PathVariable int id, Principal principal) {
        Transfer transferToUpdate = getTransferById(id);

        if (id != transferToUpdate.getTransferId() && transferToUpdate.getTransferId() != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transfer ids are in conflict.");
        }

        transferToUpdate.setTransferId(id);

        User loggedInUser = userDao.getLoggedInUserByPrinciple(principal);
        User fromUser = transferToUpdate.getUserFrom();
        int fromUserId = fromUser.getId();
        User toUser = transferToUpdate.getUserTo();
        int toUserId = toUser.getId();

        if (fromUserId != loggedInUser.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this account.");
        }

        return transferDao.updateTransfer(transferStatus, id);
    }

}
