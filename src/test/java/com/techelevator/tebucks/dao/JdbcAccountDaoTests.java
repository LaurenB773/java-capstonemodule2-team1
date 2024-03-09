package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.security.dao.JdbcUserDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAccountDaoTests extends BaseDaoTests {

    //TODO: test for getAccount and updateBalance

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccount_returns_correct_account_with_valid_id() {

    }

    @Test
    public void getAccount_returns_null_with_invalid_id() {

    }

    @Test
    public void updateBalance_exchanges_correct_amount() {

    }
    
}
