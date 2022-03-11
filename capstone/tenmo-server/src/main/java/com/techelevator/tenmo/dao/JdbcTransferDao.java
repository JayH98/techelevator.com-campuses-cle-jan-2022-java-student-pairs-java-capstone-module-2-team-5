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


    public JdbcTransferDao(JdbcTemplate jdbctemplate) {
        this.jdbcTemplate = jdbctemplate;
    }

    @Override
    public void transferMoney(int fromUser, int toUser, double amountToTransfer){
        checkUserExists(fromUser);
        checkUserExists(toUser);

        String sql = "UPDATE account SET balance = balance - ? " +
                "WHERE user_id = ? Returning account_id;";
        int fromUserAccountId = jdbcTemplate.queryForObject(sql, Integer.class, amountToTransfer, fromUser);

        sql = "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ? Returning account_id;";
        int toUserAccountId = jdbcTemplate.queryForObject(sql, Integer.class, amountToTransfer, toUser);

        createTransfer(fromUserAccountId, toUserAccountId, amountToTransfer);
    }

    @Override
    public List<Transfer> viewTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE account_from = ? OR account_to = ?;";
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId, userId);

        while (rowset.next()) {
            Transfer transfer = mapTransferToRowset(rowset);
            transfer.setAccountFromUsername(getUserRowset(transfer.getAccountFromId()).getString("username"));
            transfer.setAccountToUsername(getUserRowset(transfer.getAccountToID()).getString("username"));

            transfers.add(transfer);
        }
        return transfers;
    }

    private void createTransfer(int fromUserAccountId, int toUserAccountId, double transferAmount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?);";

        jdbcTemplate.update(sql, TransferType.SEND,
                TransferStatus.APPROVED, fromUserAccountId, toUserAccountId, transferAmount);
    }

    private void checkUserExists(int userId) {
        String sql = "SELECT user_id, username FROM tenmo_user WHERE user_id = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);

        if (!rowset.next()) {
            throw new UsernameNotFoundException("UserId " + userId + " was not found");
        }
    }

    private Transfer mapTransferToRowset(SqlRowSet rowset) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(rowset.getInt("transfer_id"));
        transfer.setTransferTypeId(rowset.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowset.getInt("transfer_status_id"));
        transfer.setAccountFromId(rowset.getInt("account_from"));
        transfer.setAccountToId(rowset.getInt("account_to"));
        transfer.setAmount(rowset.getDouble("amount"));

        return transfer;
    }

    private SqlRowSet getUserRowset(int userId) {                 //Gets usernames for Transfer object using account_id
        String sql = "SELECT username FROM tenmo_user " +
                "JOIN account ON tenmo_user.user_id = account.user_id " +
                "WHERE account_id = ?";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);
        rowset.next();                                            // Used to prevent java.sql.SQLException "Invalid cursor position"

        return rowset;
    }



}
