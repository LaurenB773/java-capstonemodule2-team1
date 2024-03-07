package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JdbcAccountDao implements AccountDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountBalance(int id) {
        Account accountBalance = new Account();
        String sql = "select * from accounts where user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                accountBalance.setAccountId(results.getInt("account_id"));
                accountBalance.setUserId(results.getInt("user_id"));
                accountBalance.setBalance(results.getDouble("balance"));
                return accountBalance;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    public void updateBalanceSend(int fromUserId, int toUserId, double amount) {

        String sql = "start transaction; " +
                "update accounts set balance = (balance - ?) " +
                "where user_id = ?; " +
                "update accounts set balance = (balance + ?) " +
                "where user_id = ?; " +
                "commit;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, fromUserId, amount, toUserId, amount);


        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    public void updateBalanceRequest(int fromUserId, int toUserId, double amount) {

        String sql = "start transaction; " +
                "update accounts set balance = (balance + ?) " +
                "where user_id = ?; " +
                "update accounts set balance = (balance - ?) " +
                "where user_id = ?; " +
                "commit;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, fromUserId, amount, toUserId, amount);

            if (rowsAffected == 2) {
                throw new ResponseStatusException(HttpStatus.ACCEPTED);
            } else {
                throw new DaoException("Zero rows affected, expected at least two.");
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

}
