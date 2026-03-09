package com.example.proiectcalendarumfst;

import java.io.Serializable;

public class Eveniment implements Serializable {
    private String titlu;
    private String data;
    private String ora;
    private String locatie;
    private String categorie;
    private String note;


    public Eveniment(String titlu, String data, String ora, String locatie, String categorie, String note) {
        this.titlu = titlu;
        this.data = data;
        this.ora = ora;
        this.locatie = locatie;
        this.categorie = categorie;
        this.note = note;

    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
