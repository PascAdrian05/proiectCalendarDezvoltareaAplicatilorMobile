package AndroidCalendar.backend.Utilizator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceUtilizatori {
    private final RepositoryUtilizatori repositoryUtilizatori;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    public ResponseEntity<DtoResponseUtilizator> adaugareUtilizator(DTOutilizatori dto){
        Utilizatori utilizatori=new Utilizatori();
        utilizatori.setEmail(dto.email());
        utilizatori.setNume(dto.nume());
        String parola=encoder.encode(dto.parola());
        utilizatori.setPassword(parola);
        repositoryUtilizatori.save(utilizatori);
        String token=jwtService.generareJWT(utilizatori);
        DtoResponseUtilizator dtoResponseUtilizator=new DtoResponseUtilizator(token,utilizatori.getNume(),utilizatori.getEmail());
        return new ResponseEntity<>(dtoResponseUtilizator, HttpStatus.OK);
    }

    public List<Utilizatori> cautareID(){
        return repositoryUtilizatori.findAll();
    }

    public ResponseEntity<DtoResponseUtilizator> verifLogare(DtoUtilizatorLogin dtoUtilizatorLogin){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(dtoUtilizatorLogin.nume(),dtoUtilizatorLogin.parola());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        Utilizatori utilizator = repositoryUtilizatori.findByNume(dtoUtilizatorLogin.nume()).orElseThrow();
        String token=jwtService.generareJWT(utilizator);
        DtoResponseUtilizator dtoResponseUtilizator=new DtoResponseUtilizator(token, utilizator.getNume(),utilizator.getEmail());
        return new ResponseEntity<>(dtoResponseUtilizator,HttpStatus.OK);


    }





}
