package com.carrental.models;

public class Reservation {
    private int id;
    private int clientId;
    private int carId;
    private String dateDebut;
    private String dateFin;
    private double prixTotal;
    private String statut;
    private String clientName;
    private String carName;

    public Reservation(int id, int clientId, int carId, String dateDebut,
                       String dateFin, double prixTotal, String statut) {
        this.id = id;
        this.clientId = clientId;
        this.carId = carId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }
    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }
}