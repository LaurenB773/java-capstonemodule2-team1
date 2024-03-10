package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;


public class JdbcTransferDaoTests extends BaseDaoTests {
    private static User user1;
    private static User user2;
    private static User user3;


    public static final Transfer TRANSFER_1 = new Transfer(1, user1, user2, 20.0, "Send");
    public static final Transfer TRANSFER_2 = new Transfer(2, user2, user3, 1000.0, "Request");
    public static final Transfer TRANSFER_3 = new Transfer(3, user2, user1, 100.0, "Send");

    //TODO: testing for getAccountTransfers, getTransferById, createTransfer, updateTransfer

    private JdbcTransferDao sut;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcUserDao userDao = new JdbcUserDao(jdbcTemplate);
        JdbcAccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
        sut = new JdbcTransferDao(jdbcTemplate, userDao, accountDao);
    }
    @Test
    public void getAccountTransfers_returns_a_transfer_given_valid_id(){
       List <Transfer> retrievedTransfers = sut.getAccountTransfers(2);
        Assert.assertEquals(2, retrievedTransfers.size());
        assertTransfersMatch(TRANSFER_2, retrievedTransfers.get(0));
        assertTransfersMatch(TRANSFER_3, retrievedTransfers.get(1));
    }
    @Test
    public void getTransferById_returns_a_transfer_given_valid_id(){
        Transfer retrievedTransfer  = sut.getTransferById(1);

        assertTransfersMatch(TRANSFER_1, retrievedTransfer);
    }
    @Test
    public void getTransferById_returns_null_given_invalid_id() {
        Transfer retrievedTransfer  = sut.getTransferById(-1);

        Assert.assertNull(retrievedTransfer);
    }
    @Test
    public void createTransfer_creates_a_transfer(){
        NewTransferDto testTransfer = new NewTransferDto();

        Transfer createdTransfer = sut.createTransfer(testTransfer);

        Assert.assertNotNull(createdTransfer);

        int transferId = createdTransfer.getTransferId();
        Assert.assertTrue(transferId > 0);

        Transfer retrievedTransfer = sut.getTransferById(transferId);
        assertTransfersMatch(createdTransfer, retrievedTransfer);

    }
    @Test
    public void updateTransfer_updates_a_transfer(){
        TransferStatusUpdateDto updateDto = new TransferStatusUpdateDto();
        Transfer transfer = sut.getTransferById(1);
        updateDto.setTransferStatus("Pending");

        Assert.assertNotNull(transfer);
        double originalAmount = transfer.getAmount();

        Transfer updatedTransfer = sut.updateTransfer(updateDto, transfer.getTransferId());

        Assert.assertNotNull(updatedTransfer);

        transfer.setAmount(originalAmount - 100);

        assertTransfersMatch(updatedTransfer, transfer);
    }
    private static void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getUserFrom(), actual.getUserFrom());
        Assert.assertEquals(expected.getUserTo(), actual.getUserTo());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
        Assert.assertEquals(expected.getTransferType(), actual.getTransferType());
    }

}
