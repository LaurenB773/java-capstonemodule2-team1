package com.techelevator.tebucks.security.controller;

import com.techelevator.tebucks.exception.DaoException;
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
public class UserController {
    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/users")
    public List<User> getUserList(Principal principal) {
        User loggedInUser = userDao.getLoggedInUserByPrinciple(principal);
        int userId = loggedInUser.getId();
        List<User> users = userDao.getUserList(userId);

        if (users.isEmpty()) {
            throw new DaoException("No users to display.");
        }

        return users;
    }

}
