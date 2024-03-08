package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public List<Transfer> getAccountTransfers(int userFromId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfers " +
                "where user_from_id = ? " +
                "order by transfer_id;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userFromId);

            while (results.next()) {
                Transfer transfer = mapToTransfer(results);
                transfers.add(transfer);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return transfers;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        String sql = "select * from transfers where transfer_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

            if (results.next()) {
                return mapToTransfer(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        }

        return null;
    }


    @Override
    public Transfer createTransfer(NewTransferDto newTransfer) {
        String sql = "insert into transfers (user_from_id, user_to_id, amount_to_transfer, transfer_type) " +
                "values(?, ?, ?, ?) returning transfer_id;";

        int userFrom = newTransfer.getUserFrom();
        int userTo = newTransfer.getUserTo();
        double amount = newTransfer.getAmount();
        String type = newTransfer.getTransferType();

        try {
            Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                    newTransfer.getUserFrom(), newTransfer.getUserTo(),
                    newTransfer.getAmount(), newTransfer.getTransferType());

            if (transferId == null) {
                throw new DaoException("Could not create transfer.");
            }

            if (type.equals("Request")) {
                sql = "UPDATE transfers " +
                        "SET status = 'Pending' " +
                        "WHERE transfer_id = ?;";

                int rowsAffected = jdbcTemplate.update(sql, transferId);

                if (rowsAffected == 0) {
                    throw new DaoException("Zero rows affected, expecting at least one.");
                }
            }
            User fromUser = userDao.getUserById(userFrom);
            User toUser = userDao.getUserById(userTo);

            Account fromUserAccount = accountDao.getAccount(fromUser.getId());
            Account toUserAccount = accountDao.getAccount(toUser.getId());


            if (type.equals("Send") && (amount < 0 || amount > fromUserAccount.getBalance())) {
                sql = "UPDATE transfers " +
                        "SET status = 'Rejected' " +
                        "WHERE transfer_id = ?;";

                int rowsAffected = jdbcTemplate.update(sql, transferId);

                if (rowsAffected == 0) {
                    throw new DaoException("Zero rows affected, expecting at least one.");
                }
            }

            return getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

    }

    @Override
    public Transfer updateTransfer(TransferStatusUpdateDto transfer, int transferId) {
        Transfer updatedTransfer = getTransferById(transferId);
        String sql = "update transfers set status = ? where transfer_id = ?;";

        try {
            int rowsAffected = jdbcTemplate.update(sql, transfer.getTransferStatus(), transferId);

            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one.");
            }

            accountDao.updateBalance(updatedTransfer.getUserFrom().getId(),
                    updatedTransfer.getUserTo().getId(), updatedTransfer.getAmount());

            updatedTransfer = getTransferById(updatedTransfer.getTransferId());

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedTransfer;
    }

    private Transfer mapToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        int userFrom = results.getInt("user_from_id");
        User fromUser = userDao.getUserById(userFrom);
        transfer.setUserFrom(fromUser);
        int userTo = results.getInt("user_to_id");
        User toUser = userDao.getUserById(userTo);
        transfer.setUserTo(toUser);
        transfer.setAmount(results.getDouble("amount_to_transfer"));
        transfer.setTransferStatus(results.getString("status"));
        transfer.setTransferType(results.getString("transfer_type"));
        return transfer;
    }

}
