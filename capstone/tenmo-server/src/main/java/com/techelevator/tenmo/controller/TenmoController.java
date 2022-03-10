package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {
    private UserDao userDao;
    private JdbcAccountDao accountDao;

    public TenmoController(JdbcUserDao userdao, JdbcAccountDao accountDao) {
        this.userDao = userdao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "users/{username}", method = RequestMethod.GET)
    public int findIdByUsername(@PathVariable String username) {
        return userDao.findIdByUsername(username);
    }

    // path = "users/username/{id}"
    @RequestMapping(path = "users/{username}/balance", method = RequestMethod.GET)
    public Double getBalance(@PathVariable String username) {
        //TODO make JdbcAccountDao query database and return that method here!
        return accountDao.getBalance(username); // This is a stub as a placeholder so the code compiles
    }
}























