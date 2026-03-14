package AndroidCalendar.backend.Utilizator;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ControllerUtilizatori {

    ServiceUtilizatori serviceUtilizatori;

    public  ControllerUtilizatori(ServiceUtilizatori serviceUtilizatori){
        this.serviceUtilizatori=serviceUtilizatori;
    }



    @PostMapping("/api/auth/register")
    public ResponseEntity<DtoResponseUtilizator> adaugareUtilizator(@Valid @RequestBody DTOutilizatori dto){
        return serviceUtilizatori.adaugareUtilizator(dto);
    }





    @GetMapping("/api/auth/utilizator")
    public List<Utilizatori> cautareUtilizator(){
        return serviceUtilizatori.cautareID();
    }

    @PostMapping("/api/auth/logare")
    public ResponseEntity<DtoResponseUtilizator> logareCont(@RequestBody DtoUtilizatorLogin dtoUtilizatorLogin){
        return serviceUtilizatori.verifLogare(dtoUtilizatorLogin);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> create(MethodArgumentNotValidException e){
        Map<String,String> errors=new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(err->{
            String val=err.getField();
            String mesaj=err.getDefaultMessage();
            errors.put(val,mesaj);

        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }




}
