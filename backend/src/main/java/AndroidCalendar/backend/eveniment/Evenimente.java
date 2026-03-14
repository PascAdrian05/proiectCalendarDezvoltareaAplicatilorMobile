package AndroidCalendar.backend.eveniment;

import AndroidCalendar.backend.Utilizator.Utilizatori;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Evenimente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @NotBlank
    private String titlu;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate data;

    @NotBlank
    private String ora;

    private String locatie;

    private String categore;

    private String note;

    @ManyToOne
    @JoinColumn(name = "utilizator_id")
    @JsonBackReference
    private Utilizatori utilizator;

    public Evenimente() {
    }

    public Evenimente(Integer id, Utilizatori utilizator, String note, String categore, String locatie, String ora, LocalDate data, String titlu) {
        this.id = id;
        this.utilizator = utilizator;
        this.note = note;
        this.categore = categore;
        this.locatie = locatie;
        this.ora = ora;
        this.data = data;
        this.titlu = titlu;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public String getCategore() {
        return categore;
    }

    public void setCategore(String categore) {
        this.categore = categore;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Utilizatori getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizatori utilizator) {
        this.utilizator = utilizator;
    }
}
