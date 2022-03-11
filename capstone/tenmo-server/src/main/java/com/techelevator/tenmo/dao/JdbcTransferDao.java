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
    public List<Transfer> viewTransfers(int id) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE account_from = ? OR account_to = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, id, id);

        while (rowset.next()) {
            Transfer transfer = mapTransferToRowset(rowset);
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
        transfer.setAccountFrom(rowset.getInt("account_from"));
        transfer.setAccountTo(rowset.getInt("account_to"));
        transfer.setAmount(rowset.getDouble("amount"));

        return transfer;
    }




}
