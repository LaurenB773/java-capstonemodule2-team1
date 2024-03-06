package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getAccountTransfers(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfers where user_from_id = ?;";
        String sql2 = "select * from transfers where user_to_id  = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);

            while (results.next()) {
                Transfer transfer = mapToTransfer(results);

                transfers.add(transfer);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql2, accountId);

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
    public Transfer createTransfer() {
        //TODO: implement create transfer - post
        return null;
    }

    @Override
    public Transfer updateTransfer() {
        //TODO: implement update transfer - put
        return null;
    }
    private static Transfer mapToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setUserFromId(results.getInt("user_from_id"));
        transfer.setUserToId(results.getInt("user_to_id"));
        transfer.setAmountToTransfer(results.getDouble("amount_to_transfer"));
        transfer.setSucessful(results.getBoolean("is_sucessful"));
        return transfer;
    }

}
