package com.techelevator.tenmo.model;

public enum TransferStatus {
    PENDING(1),
    APPROVED(2),
    REJECTED(3);

    private int transferStatusCode;

    TransferStatus(int transferStatusCode) {
        this.transferStatusCode = transferStatusCode;
    }

    public int getTransferStatusCode() { return transferStatusCode; }
}
