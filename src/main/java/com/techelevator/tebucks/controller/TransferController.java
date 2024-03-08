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

    @GetMapping("/account/balance")
    public Account getAccount(Principal principal) {
        User loggedInUser = getLoggedInUserByPrincipal(principal);
        int userId = loggedInUser.getId();
        return accountDao.getAccount(userId);
    }

    @GetMapping("/account/transfers")
    public List<Transfer> getAccountTransfers(Principal principal) {
        User loggedInUser = getLoggedInUserByPrincipal(principal);
        int userId = loggedInUser.getId();
        return transferDao.getAccountTransfers(userId);
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

        if (createdTransfer.getTransferType().equals("Send")) {
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

        User loggedInUser = getLoggedInUserByPrincipal(principal);
        User transferUser = transferToUpdate.getUserFrom();
        int idTransferUserFrom = transferUser.getId();
        User transferToUser = transferToUpdate.getUserTo();
        int idTransferUserTo = transferToUser.getId();

        if (idTransferUserFrom != loggedInUser.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this account.");
        }

        try {
            if (transferStatusUpdateDto.getTransferStatus().equals("Approved")) {
                accountDao.updateBalance(idTransferUserFrom, idTransferUserTo, transferToUpdate.getAmount());
            } else if (transferStatusUpdateDto.getTransferStatus().equals("Rejected")) {
                throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Request rejected.");
            }
            return transferDao.updateTransfer(transferStatus, id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        }
    }

    private User getLoggedInUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userDao.getUserByUsername(username);
    }

    @GetMapping("/users")
    public List<User> getUserList(Principal principal) {
        User loggedInUser = getLoggedInUserByPrincipal(principal);
        int userId = loggedInUser.getId();
        List<User> users = userDao.getUserList(userId);

        if (users.isEmpty()) {
            throw new DaoException("No users to display.");
        }

        return users;
    }

}
