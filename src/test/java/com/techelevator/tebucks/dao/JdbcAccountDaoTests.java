package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAccountDaoTests extends BaseDaoTests {
    private static final Account ACCOUNT_1 = new Account(1, 1, 1000.00);
    private static final Account ACCOUNT_2 = new Account(2, 2, 500.00);

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccount_returns_correct_account_with_valid_id() {
        Account account1 = sut.getAccount(1);
        asserAccountsMatch(ACCOUNT_1, account1);

        Account account2 = sut.getAccount(2);
        asserAccountsMatch(ACCOUNT_2, account2);
    }

    @Test
    public void getAccount_returns_null_with_invalid_id() {
        Account account = sut.getAccount(-1);
        Assert.assertNull(account);
    }

    private void asserAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance());
    }

}
