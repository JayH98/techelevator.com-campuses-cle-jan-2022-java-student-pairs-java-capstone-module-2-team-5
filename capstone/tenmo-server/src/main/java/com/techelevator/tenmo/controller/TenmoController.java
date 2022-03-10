package com.techelevator.tenmo.controller;


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

    public TenmoController(JdbcUserDao userdao) {
        this.userDao = userdao;
    }

    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "users/{username}", method = RequestMethod.GET)
    public int findIdByUsername(@PathVariable String username) {
        return userDao.findIdByUsername(username);
    }







}























