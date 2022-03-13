package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public void transferMoney(Transfer transfer);

    public List<Transfer> viewTransfers(int userId) throws TransferNotFoundException;

    public List<Transfer> viewPendingTransferRequests(int userId) throws TransferNotFoundException;

    public Transfer createTransfer(Transfer transfer);



// TODO implement method to update pending requests to either rejected or accepted
//    public Transfer updateTransfer(Transfer transfer);


}






