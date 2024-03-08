package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.security.dao.JdbcUserDao;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAccountDaoTests extends BaseDaoTests {

    //TODO: test for getAccount and updateBalance

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }
}
