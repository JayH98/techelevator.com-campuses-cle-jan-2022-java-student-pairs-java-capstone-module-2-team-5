package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

    public void transferMoney(int transferTypeId, int transferStatusId,
                              int accountFrom, int accountTo, double amount);

}






