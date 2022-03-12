package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public void transferMoney(int fromUsername, int toUsername, double amountToTransfer);

    public List<Transfer> viewTransfers(int userId) throws TransferNotFoundException;


}






