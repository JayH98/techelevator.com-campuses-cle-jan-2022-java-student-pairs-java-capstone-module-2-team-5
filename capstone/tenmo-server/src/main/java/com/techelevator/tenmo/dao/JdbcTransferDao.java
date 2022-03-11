package com.techelevator.tenmo.dao;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbctemplate;

    public JdbcTransferDao(JdbcTemplate jdbctemplate) {
        this.jdbctemplate = jdbctemplate;
    }

    @Override
    public void transferMoney(int transferTypeId, int transferStatusId,
                              int accountFrom, int accountTo, double amount){

    }
}
