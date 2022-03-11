package com.techelevator.tenmo.dao;

public interface TransferDao {

    public void transferMoney(int transferTypeId, int transferStatusId,
                              int accountFrom, int accountTo, double amount);

}






