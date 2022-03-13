package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public void transferMoney(long fromUsername, long toUsername, double amountToTransfer);

    public List<Transfer> viewTransfers(int userId) throws TransferNotFoundException;

    public Transfer requestMoney(long fromUsername, long toUsername, double amountToTransfer);


}






