package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public void transferMoney(String fromUsername, String toUsername, double amountToTransfer);

    public List<Transfer> viewTransfers(String username);


}






