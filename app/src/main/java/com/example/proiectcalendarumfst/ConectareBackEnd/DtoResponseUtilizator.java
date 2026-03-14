package com.example.proiectcalendarumfst.ConectareBackEnd;

public class DtoResponseUtilizator {
    private String token;
    private String nume;
    private String email;

    public DtoResponseUtilizator() {
    }

    public DtoResponseUtilizator(String token, String nume, String email) {
        this.token = token;
        this.nume = nume;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
