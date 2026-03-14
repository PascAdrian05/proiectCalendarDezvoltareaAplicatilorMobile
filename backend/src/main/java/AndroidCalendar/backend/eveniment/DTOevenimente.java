package AndroidCalendar.backend.eveniment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record   DTOevenimente(
        int id,
        @NotBlank
        String titlu,
        @NotNull
        String data,
        @NotBlank
        String ora,
        String locatie,
        String categorie,
        String note

) {
}
