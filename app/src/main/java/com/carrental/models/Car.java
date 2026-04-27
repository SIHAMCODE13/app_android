package com.carrental.models;

public class Car {
    private int id;
    private String marque;
    private String modele;
    private int annee;
    private double prixJour;
    private boolean disponible;

    public Car(int id, String marque, String modele, int annee, double prixJour, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.prixJour = prixJour;
        this.disponible = disponible;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public double getPrixJour() { return prixJour; }
    public void setPrixJour(double prixJour) { this.prixJour = prixJour; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return marque + " " + modele + " (" + annee + ")";
    }
}