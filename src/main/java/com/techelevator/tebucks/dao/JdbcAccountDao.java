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
    public Account getAccount(int id) {
        Account account = new Account();
        String sql = "select * from accounts where user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account.setAccountId(results.getInt("account_id"));
                account.setUserId(results.getInt("user_id"));
                account.setBalance(results.getDouble("balance"));
                return account;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return null;
    }

    public void updateBalance(int fromUserId, int toUserId, double amount) {

        String sql = "start transaction; " +
                "update accounts set balance = (balance - ?) " +
                "where user_id = ?; " +
                "update accounts set balance = (balance + ?) " +
                "where user_id = ?; " +
                "commit;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, amount, fromUserId, amount, toUserId);


        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

}
