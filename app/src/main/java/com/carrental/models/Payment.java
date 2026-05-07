package com.carrental.models;

public class Payment {
    private int id;
    private int reservationId;
    private int clientId;
    private double amount;
    private String paymentDate;
    private String clientName; // For display purposes
    private String carName;   // For display purposes

    public Payment(int id, int reservationId, int clientId, double amount, String paymentDate) {
        this.id = id;
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }
}