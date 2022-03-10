package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

public interface AccountDao {
    // should this parameter be account_id if a user has multiple accounts?
    public Double getBalance(String username);
}
