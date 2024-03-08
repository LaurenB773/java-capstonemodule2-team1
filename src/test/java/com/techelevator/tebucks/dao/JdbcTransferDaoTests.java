package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.dao.UserDao;
import org.junit.Before;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTransferDaoTests extends BaseDaoTests {

    //TODO: testing for getAccountTransfers, getTransferById, createTransfer, updateTransfer

    private JdbcTransferDao sut;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcUserDao userDao = new JdbcUserDao(jdbcTemplate);
        JdbcAccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
    }
}
