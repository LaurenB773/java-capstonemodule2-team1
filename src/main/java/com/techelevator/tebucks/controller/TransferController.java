package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
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

    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @GetMapping("/account/balance")
    public Account getAccountBalance(Principal principal) {
        User loggedInUser = getLoggedInUserByPrincipal(principal);
        int userId = loggedInUser.getId();
        return accountDao.getAccountBalance(userId);
    }

    @GetMapping("/account/transfers")
    public List<Transfer> getAccountTransfer(Principal principal) {
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
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer) {
        return transferDao.createTransfer(transfer);
    }

    @PutMapping("/transfers/{id}/status")
    public Transfer updateTransfer(@Valid @RequestBody Transfer transferToUpdate,
                                   @PathVariable int id) {
        if (id != transferToUpdate.getTransferId() && transferToUpdate.getTransferId() != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transfer ids are in conflict.");
        }

        transferToUpdate.setTransferId(id);

        try {
            return transferDao.updateTransfer(transferToUpdate);
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
        List<User> users = userDao.getUserList();

        for (User user : users) {
            if (user == loggedInUser) {
                users.remove(user);
            }
        }

        return users;
    }

}
