package com.example.shaktisetu;

public class Payment {
    public String workerName;
    public String clientName;
    public String status; // PENDING / PAID
    public long timestamp;

    public Payment(String workerName, String clientName, String status, long timestamp) {
        this.workerName = workerName;
        this.clientName = clientName;
        this.status = status;
        this.timestamp = timestamp;
    }
}
