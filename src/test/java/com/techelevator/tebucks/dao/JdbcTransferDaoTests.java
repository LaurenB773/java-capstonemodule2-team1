package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


public class JdbcTransferDaoTests extends BaseDaoTests {
    private static User user1;
    private static User user2;
    private static User user3;


    public static final Transfer TRANSFER_1 = new Transfer(1, user1, user2, 20.0, "Send");
    public static final Transfer TRANSFER_2 = new Transfer(2, user3, user1, 1000.0, "Request");
    public static final Transfer TRANSFER_3 = new Transfer(3, user2, user1, 100.0, "Send");

    //TODO: testing for getAccountTransfers, getTransferById, createTransfer, updateTransfer

    private JdbcTransferDao sut;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcUserDao userDao = new JdbcUserDao(jdbcTemplate);
        JdbcAccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
    }
    @Test
    public void getAccountTransfers_returns_a_transfer_given_valid_id(){
       List <Transfer> retrievedTransfers = sut.getAccountTransfers(1);
        Assert.assertEquals(1, retrievedTransfers.size());
        assertTransfersMatch(TRANSFER_1, retrievedTransfers.get(0));
    }
    @Test
    public void getTransferById_returns_a_transfer_given_valid_id(){

    }
    @Test
    public void createTransfer_creates_a_transfer(){

    }
    @Test
    public void updateTransfer_updates_a_transfer(){

    }
    private static void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getUserFrom(), actual.getUserFrom());
        Assert.assertEquals(expected.getUserTo(), actual.getUserTo());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
        Assert.assertEquals(expected.getTransferType(), actual.getTransferType());
    }

}
