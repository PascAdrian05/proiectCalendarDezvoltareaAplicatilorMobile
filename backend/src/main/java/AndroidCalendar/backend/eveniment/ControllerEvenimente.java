package AndroidCalendar.backend.eveniment;


import AndroidCalendar.backend.Utilizator.Utilizatori;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ControllerEvenimente {

    private final ServiceEvenimente serviceEvenimente;
    public ControllerEvenimente(ServiceEvenimente serviceEvenimente){
        this.serviceEvenimente=serviceEvenimente;
    }

    @PostMapping("/eveniment")
    public ResponseEntity<DTOevenimente> adaugareEveniment(@Valid @RequestBody DTOevenimente dto, @AuthenticationPrincipal Utilizatori utilizatorCurent) {

        DTOevenimente evenimentSalvat = serviceEvenimente.adaugareEveniment(dto, utilizatorCurent);

        return ResponseEntity.ok(evenimentSalvat);
    }

    @DeleteMapping("/eveniment/{id}")
    public ResponseEntity<Void> stergereEveniment(@AuthenticationPrincipal Utilizatori utilizatori,@PathVariable int id){
        boolean sters= serviceEvenimente.stergereEveniment(utilizatori,id);
        if(sters){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }

    @PutMapping("/eveniment/{id}")
    public ResponseEntity<DTOevenimente> updateEveniment(
            @AuthenticationPrincipal Utilizatori utilizator,
            @PathVariable("id") int idEveniment,
            @Valid @RequestBody DTOevenimente dto){
        return new ResponseEntity<>(serviceEvenimente.updateEveniment(utilizator,idEveniment,dto),HttpStatus.OK);
    }


    @GetMapping("/eveniment")
    public ResponseEntity<List<DTOevenimente>> evenimenteUtilizatorului(@AuthenticationPrincipal Utilizatori utilizatori){
        return new ResponseEntity<>(serviceEvenimente.listaEvenimente(utilizatori),HttpStatus.OK);
    }

    @GetMapping("/api/auth/eveniment")
    public List<Evenimente> evenimentes(){
        return serviceEvenimente.afisareTot();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handler(MethodArgumentNotValidException ex){
        Map<String,String> exceptii=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e->{
            String val=e.getField();
            String msg=e.getDefaultMessage();

            exceptii.put(val,msg);
        });
        return new ResponseEntity<>(exceptii, HttpStatus.BAD_REQUEST);
    }


}
