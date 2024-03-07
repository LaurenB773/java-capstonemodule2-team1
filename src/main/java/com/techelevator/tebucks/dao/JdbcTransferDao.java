package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
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
    public Transfer createTransfer(NewTransferDto newTransfer) {
        String sql = "insert into transfers (user_from_id, user_to_id, amount_to_transfer) " +
                "values(?, ?, ?) returning transfer_id;";

        try {
            Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                    newTransfer.getUserFrom(), newTransfer. getUserTo(),
                    newTransfer.getAmount());

            if (transferId == null) {
                throw new DaoException("Could not create transfer.");
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
        String sql = "update transfers set user_from_id = ?, user_to_id = ?, " +
                "amount_to_transfer = ?, status = ? where transfer_id = ?;";

        //TODO: add status from transfer status DTO

        try {
            int rowsAffected = jdbcTemplate.update(sql, updatedTransfer.getUserFromId(), updatedTransfer.getUserToId(),
                    updatedTransfer.getAmountToTransfer(), transfer.getTransferStatus());

            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one.");
            }

            updatedTransfer = getTransferById(updatedTransfer.getTransferId());

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
        return transfer;
    }

}
