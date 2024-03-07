package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Transfer;
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

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getAccountTransfers(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfers " +
                "join account_transfers using (transfer_id) where account_id = ? " +
                "order by transfer_id;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);

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
    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer;
        String sql = "insert into transfers (user_from_id, user_to_id, amount_to_transfer, is_successful) " +
                "values(?, ?, ?, ?) returning transfer_id;";

        try {
            Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                    transfer.getUserFromId(), transfer.getUserToId(),
                    transfer.getAmountToTransfer(), transfer.isSuccessful());

            if (transferId == null) {
                throw new DaoException("Could not create transfer.");
            }

            newTransfer = getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return newTransfer;
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        Transfer updatedTransfer = null;
        String sql = "update transfers set user_from_id = ?, user_to_id = ?, " +
                "amount_to_transfer = ?, is_successful = ? where transfer_id = ?;";

        try {
            int rowsAffected = jdbcTemplate.update(sql, transfer.getUserFromId(),
                    transfer.getUserToId(), transfer.getAmountToTransfer(),
                    transfer.isSuccessful(), transfer.getTransferId());

            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one.");
            }

            updatedTransfer = getTransferById(transfer.getTransferId());

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedTransfer;
    }

    private static Transfer mapToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setUserFromId(results.getInt("user_from_id"));
        transfer.setUserToId(results.getInt("user_to_id"));
        transfer.setAmountToTransfer(results.getDouble("amount_to_transfer"));
        transfer.setSuccessful(results.getBoolean("is_successful"));
        return transfer;
    }

}
