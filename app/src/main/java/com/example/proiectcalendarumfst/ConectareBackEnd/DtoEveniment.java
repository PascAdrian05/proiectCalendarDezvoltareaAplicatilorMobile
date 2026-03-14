package com.example.proiectcalendarumfst.ConectareBackEnd;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DtoEveniment implements Serializable {
    private int id;
    private String titlu;
    private String data;
    private String ora;
    private String locatie;
    private String categorie;
    private String note;

    public DtoEveniment() {
    }

    public DtoEveniment(int id, String titlu, String data, String ora, String locatie, String categorie, String note) {
        this.id = id;
        this.titlu = titlu;
        this.data = data;
        this.ora = ora;
        this.locatie = locatie;
        this.categorie = categorie;
        this.note = note;
    }

    public DtoEveniment(String titlu, String data, String ora, String locatie, String categorie, String note) {
        this.titlu = titlu;
        this.data = data;
        this.ora = ora;
        this.locatie = locatie;
        this.categorie = categorie;
        this.note = note;
    }

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getOra() { return ora; }
    public void setOra(String ora) { this.ora = ora; }

    public String getLocatie() { return locatie; }
    public void setLocatie(String locatie) { this.locatie = locatie; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DtoEveniment that = (DtoEveniment) o;
        return Objects.equals(titlu, that.titlu) && 
               Objects.equals(data, that.data) && 
               Objects.equals(ora, that.ora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titlu, data, ora);
    }
}
