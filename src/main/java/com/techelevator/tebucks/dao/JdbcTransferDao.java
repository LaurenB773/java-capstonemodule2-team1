package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.TEARS.TearsLogDto;
import com.techelevator.tebucks.TEARS.TearsService;
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
    private TearsService tearsService = new TearsService();


    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public List<Transfer> getAccountTransfers(int loggedInUserId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfers " +
                "where user_from_id = ? or user_to_id = ? " +
                "order by transfer_id;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, loggedInUserId, loggedInUserId);

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
    public Transfer createTransfer(NewTransferDto newTransferDto) {
        String sql = "insert into transfers " +
                "(user_from_id, user_to_id, amount_to_transfer, status, transfer_type) " +
                "values(?, ?, ?, ?, ?) returning transfer_id;";

        String status = "Pending";

        if (newTransferDto.getTransferType().equals("Send")) {
            status = getStatus(newTransferDto, isTransferValid(newTransferDto));
        }

        Transfer createdTransfer;
        try {
            Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                    newTransferDto.getUserFrom(), newTransferDto.getUserTo(), newTransferDto.getAmount(),
                    status, newTransferDto.getTransferType());

            if (transferId == null) {
                throw new DaoException("Could not create transfer.");
            }

            createdTransfer = getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        if (createdTransfer.getTransferStatus().equals("Rejected")) {
            recordTears(addToTearsLog(createdTransfer));
        }

        return createdTransfer;
    }

    @Override
    public Transfer updateTransfer(TransferStatusUpdateDto transferStatusUpdateDto, int transferId) {
        Transfer updatedTransfer = getTransferById(transferId);
        String sql = "update transfers set status = ? where transfer_id = ?;";

        String status = transferStatusUpdateDto.getTransferStatus();

        Account userFromAccount = accountDao.getAccountByUserId(updatedTransfer.getUserFrom().getId());
        if(updatedTransfer.getAmount() > userFromAccount.getBalance()) {
            status = "Rejected";
        }

        try {
            int rowsAffected = jdbcTemplate.update(sql, status, transferId);

            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one.");
            }

            updatedTransfer = getTransferById(updatedTransfer.getTransferId());

            if (updatedTransfer.getTransferStatus().equals("Rejected")) {
                recordTears(addToTearsLog(updatedTransfer));
            } else {
                accountDao.updateBalance(updatedTransfer.getUserFrom().getId(),
                        updatedTransfer.getUserTo().getId(), updatedTransfer.getAmount());
            }

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

    private TearsLogDto addToTearsLog(Transfer transfer) {
        TearsLogDto logDto = new TearsLogDto();
        logDto.setUsername_from(transfer.getUserFrom().getUsername());
        logDto.setUsername_to(transfer.getUserTo().getUsername());
        logDto.setAmount(transfer.getAmount());
        logDto.setDescription("Transfer status rejected"); //TODO: better messages?
        return logDto;
    }

    private void recordTears(TearsLogDto tearsLogDto) {
        tearsService.addLog(tearsLogDto);
    }

    private boolean isTransferValid(NewTransferDto newTransferDto) {
        boolean isTransferValid = false;

        int userFrom = newTransferDto.getUserFrom();
        double amount = newTransferDto.getAmount();

        Account userFromBalance = accountDao.getAccountByUserId(userFrom);

        if (amount > 0 && amount < userFromBalance.getBalance()) {
            isTransferValid = true;
        }
        return isTransferValid;
    }

    private String getStatus(NewTransferDto newTransferDto, boolean isTransferValid) {
        String status = null;

        if (newTransferDto.getTransferType().equals("Send") && isTransferValid) {
           status = "Approved";
        } else if (newTransferDto.getTransferType().equals("Send") && !isTransferValid) {
            status = "Rejected";
        }
        return status;
    }

}
