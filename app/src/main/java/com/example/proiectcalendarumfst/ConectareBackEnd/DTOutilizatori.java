package com.example.proiectcalendarumfst.ConectareBackEnd;

public class DTOutilizatori {
    private String nume;
    private String email;
    private String parola;

    public DTOutilizatori() {
    }

    public DTOutilizatori(String nume, String parola) {
        this.nume = nume;
        this.parola = parola;
    }

    public DTOutilizatori(String nume, String email, String parola) {
        this.nume = nume;
        this.email = email;
        this.parola = parola;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
}
