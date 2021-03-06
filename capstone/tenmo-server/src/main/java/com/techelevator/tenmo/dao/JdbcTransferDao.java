package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.*;
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
    public void transferMoney(Transfer transfer) {
        checkUserExists(transfer.getFromUserId());
        checkUserExists(transfer.getToUserId());

        String sql = "UPDATE account SET balance = (balance - ?) " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(), transfer.getFromUserId());                   // TODO use created update balance instead

        sql = "UPDATE account SET balance = (balance + ?) " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(), transfer.getToUserId());

        transfer.setTransferTypeId(TransferType.SEND);
        transfer.setTransferStatusId(TransferStatus.APPROVED);

        createTransfer(transfer);
    }

    @Override
    public List<Transfer> viewTransfers(int userId) throws TransferNotFoundException {
        List<Transfer> transfers = new ArrayList<>();

        boolean gotRowSet = false;
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_from, account_to, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account ON transfer.account_from = account.account_id OR transfer.account_to = account.account_id " +
                "WHERE user_id = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);
        while (rowset.next()) {
            gotRowSet = true;

            Transfer transfer = mapTransferToRowset(rowset);
            transfer.setAccountFromUsername(getUserRowset(transfer.getAccountFromId()).getString("username"));   // finds username of sender
            transfer.setAccountToUsername(getUserRowset(transfer.getAccountToId()).getString("username"));       // finds username of sendee

            transfers.add(transfer);
        }
        if (gotRowSet) {
            return transfers;
        }
        throw new TransferNotFoundException("Error. No such transfer exists or you do not have permission to view it.");
    }

    @Override
    public List<Transfer> viewPendingTransferRequests(int userId) throws TransferNotFoundException {
        List<Transfer> pendingTransfers = new ArrayList<>();
        boolean gotRowSet = false;

//        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_from, account_to, amount " +      // Original SQL statement.
//                "FROM transfer " +                                                                                            // Would obtain all transfers that were pending where user
//                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +                         // was involved, not just where user was account_to
//                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
//                "JOIN account ON transfer.account_from = account.account_id OR transfer.account_to = account.account_id " +
//                "WHERE user_id = ? AND transfer.transfer_status_id = ?;";

       String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_from, account_to, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account ON transfer.account_from = account.account_id " +
                "WHERE account.user_id = ? AND transfer.transfer_status_id = ?; ";



        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, TransferStatus.PENDING);
        while (rowSet.next()) {
            gotRowSet = true;

            // Our transfer model object is bloated for convenience
            // Need to set these extra instance variables to make other parts of the codebase work
            // Client side needs username as a string, and not the user_id or transfer id as numbers
            Transfer transfer = mapTransferToRowset(rowSet);
            transfer.setAccountFromUsername(getUserRowset(transfer.getAccountFromId()).getString("username"));
            transfer.setAccountToUsername(getUserRowset(transfer.getAccountToId()).getString("username"));

            pendingTransfers.add(transfer);
        }
        // flag exception
        if (gotRowSet)
            return pendingTransfers;
        throw new TransferNotFoundException("Error. No such transfer exists or you do not have permission to view it.");
    }


    public Transfer createTransfer(Transfer transfer) {
        transfer.setAccountFromId(findAccountByUserId(transfer.getFromUserId()));
        transfer.setAccountToId(findAccountByUserId(transfer.getToUserId()));
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) returning transfer_id;";

        transfer.setTransferId(jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferTypeId(),
                transfer.getTransferStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount()));
        return transfer;
    }

    public void updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfer " +
                "SET transfer_status_id = ? " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transfer.getTransferStatusId(), transfer.getTransferId());
    }

    public void updateBalance(Transfer transfer) {                          // TODO possibly move update balance into accountDao
        String sql = "Update account " +
                "SET balance = balance - ? " +
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAccountFromId());

        sql = "Update account " +
                "SET balance = balance + ?" +
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAccountToId());
    }

    private int findAccountByUserId(long id) {
        Account account = new Account();
        String sql = "SELECT account_id " +
                "FROM account " +
                "WHERE user_id = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, id);
        if (rowset.next()) {
            account.setAccountId(rowset.getInt("account_id"));
        }
        return account.getAccountId();
    }

    private void checkUserExists(long userId) {
        String sql = "SELECT user_id, username FROM tenmo_user WHERE user_id = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);

        if (!rowset.next()) {
            throw new UsernameNotFoundException("UserId " + userId + " was not found");
        }
    }

    private Transfer mapTransferToRowset(SqlRowSet rowset) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(rowset.getInt("transfer_id"));
        transfer.setTransferType(rowset.getString("transfer_type_desc"));
        transfer.setTransferStatus(rowset.getString("transfer_status_desc"));
        transfer.setAccountFromId(rowset.getInt("account_from"));
        transfer.setAccountToId(rowset.getInt("account_to"));
        transfer.setAmount(rowset.getDouble("amount"));

        return transfer;
    }

    private SqlRowSet getUserRowset(int userId) {      //Gets usernames for Transfer object using account_id
        String sql = "SELECT username FROM tenmo_user " +
                "JOIN account ON tenmo_user.user_id = account.user_id " +
                "WHERE account_id = ?;";

        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);
        rowset.next();                      // Used to prevent java.sql.SQLException "Invalid cursor position"

        return rowset;
    }

    public void acceptRequest(Transfer transfer){
        setTransferStatusToApproved(transfer);
        subtractAmountFromSender(transfer);
        addAmountToRequester(transfer);
    }

    @Override
    public void rejectRequest(Transfer transfer) {
        String sql = "UPDATE transfer  " +
                "SET transfer_status_id = ?  " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, TransferStatus.REJECTED,
                transfer.getAccountToId());
    }


    private void setTransferStatusToApproved(Transfer transfer){
        String sql = "UPDATE transfer  " +
                "SET transfer_status_id = ?  " +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, TransferStatus.APPROVED,
                transfer.getAccountToId());

    }

    private void subtractAmountFromSender(Transfer transfer){
        String sql = "UPDATE account  " +
                "SET balance = (balance - ?)" +
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(),
                transfer.getAccountFromId());

    }

    private void addAmountToRequester(Transfer transfer){
        String sql = "UPDATE account  " +
                "SET balance = (balance + ?)" +
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, transfer.getAmount(),
                transfer.getAccountToId());

    }

}

