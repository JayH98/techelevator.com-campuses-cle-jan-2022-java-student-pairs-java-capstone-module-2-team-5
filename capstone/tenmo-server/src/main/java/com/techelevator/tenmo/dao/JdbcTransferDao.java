package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;
    private UserDao jdbcUserDao;

    // TODO talk with Ben about tightly coupled code
    public JdbcTransferDao(JdbcTemplate jdbctemplate, UserDao jdbcUserDao) {
        this.jdbcTemplate = jdbctemplate;
        this.jdbcUserDao = jdbcUserDao;
    }

    @Override
    public void transferMoney(String fromUsername, String toUsername, double amountToTransfer){
        jdbcUserDao.findByUsername(fromUsername);
        jdbcUserDao.findByUsername(toUsername);

        User fromUser = jdbcUserDao.findByUsername(fromUsername);
        User toUser = jdbcUserDao.findByUsername(toUsername);

        String sql = "UPDATE account SET balance = balance - ? " +
                "WHERE user_id = ? Returning account_id;";
        int fromUserAccountId = jdbcTemplate.queryForObject(sql, Integer.class, amountToTransfer, fromUser.getId());

        sql = "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ? Returning account_id;";
        int toUserAccountId = jdbcTemplate.queryForObject(sql, Integer.class, amountToTransfer, toUser.getId());

        createTransfer(fromUserAccountId, toUserAccountId, amountToTransfer);
    }

    @Override
    public List<Transfer> viewTransfers(String username) {
        List<Transfer> transfers = new ArrayList<>();

        return transfers;
    }

    private void createTransfer(int fromUserAccountId, int toUserAccountId, double transferAmount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?);";

        jdbcTemplate.update(sql, TransferType.SEND,
                TransferStatus.APPROVED, fromUserAccountId, toUserAccountId, transferAmount);
    }




}
