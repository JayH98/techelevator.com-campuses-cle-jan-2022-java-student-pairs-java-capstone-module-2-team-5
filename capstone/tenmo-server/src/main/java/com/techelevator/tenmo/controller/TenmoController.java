package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {
    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public TenmoController(UserDao userdao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userdao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

//    @RequestMapping(path = "users/{username}", method = RequestMethod.GET)
//    public int findIdByUsername(@PathVariable String username) {
//        return userDao.findIdByUsername(username);
//    }

    @RequestMapping(path = "users/{id}/balance", method = RequestMethod.GET)
    public Double getBalance(@PathVariable int id) {
        return accountDao.getBalance(id);
    }

    // Todo make validations for transfer model
    @RequestMapping(path = "transfers", method = RequestMethod.PUT)
    public void transfer(/*@Valid*/ @RequestBody Transfer transfer) {
        transferDao.transferMoney(transfer);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(path = "transfers/requests", method = RequestMethod.POST)
    public Transfer requestMoney(/*@Valid*/ @RequestBody Transfer transfer) {
        transfer.setAccountFromId(transferDao.findAccount(transfer.getFromUserId()));           // finds requesters account_id
        transfer.setAccountToId(transferDao.findAccount(transfer.getToUserId()));
        return transferDao.createTransfer(transfer);                                            // finds requestees account_id
    }

    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> viewTransfers(@PathVariable(name = "id") int userId) throws TransferNotFoundException {
        return transferDao.viewTransfers(userId);
    }
    // TODO ask Ben about Principal
    // TODO make id optional
    // TODO only print transfers based on Principal
}























