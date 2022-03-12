package com.techelevator.tenmo.model;

public class TransferDTO {

    private int fromUserId;
    private int toUserId;
    private double amount;

    public int getFrom() {
        return fromUserId;
    }

    public void setFrom(int from) {
        this.fromUserId = from;
    }

    public int getTo() {
        return toUserId;
    }

    public void setTo(int to) {
        this.toUserId = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
