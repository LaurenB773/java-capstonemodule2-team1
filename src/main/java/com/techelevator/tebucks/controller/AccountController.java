package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private TransferDao transferDao;
    private AccountDao accountDao;
    private UserDao userDao;

    public AccountController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @GetMapping("/account/balance")
    public Account getAccountByUserId(Principal principal) {
        User loggedInUser = userDao.getLoggedInUserByPrinciple(principal);
        int userId = loggedInUser.getId();
        return accountDao.getAccountByUserId(userId);
    }

    @GetMapping("/account/transfers")
    public List<Transfer> getAccountTransfers(Principal principal) {
        User loggedInUser = userDao.getLoggedInUserByPrinciple(principal);
        int userId = loggedInUser.getId();
        return transferDao.getAccountTransfers(userId);
    }

}
