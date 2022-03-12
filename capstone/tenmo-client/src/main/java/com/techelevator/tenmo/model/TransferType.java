package com.techelevator.tenmo.model;

public enum TransferType {
    REQUEST(1),
    SEND(2);

    private int transferTypeCode;

    TransferType(int transferTypeCode) {
        this.transferTypeCode = transferTypeCode;
    }

    public int getTransferTypeCode() { return this.transferTypeCode; }
}
