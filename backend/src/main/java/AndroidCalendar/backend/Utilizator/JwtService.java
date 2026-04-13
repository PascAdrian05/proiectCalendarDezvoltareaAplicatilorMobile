package AndroidCalendar.backend.Utilizator;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secret;

    public String generareJWT(Utilizatori utilizatori){
        return Jwts.builder().subject(utilizatori.getUsername()).claim("email",utilizatori.getEmail()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis()+1000*60*24)).signWith(secretsemnat()).compact();
    }


    //extragere payload
    private Claims extractClaims(String token){
        return Jwts.parser().verifyWith(secretsemnat()).build().parseSignedClaims(token).getPayload();
    }

    //extragere din payload a usernameului;


    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    public Date extragereTimp(String token){
        return extractClaims(token).getExpiration();
    }


    public boolean verificareToken(String token, UserDetails utilizator){
        String usrExtract=extractUsername(token);
        if(usrExtract.equals(utilizator.getUsername())){
            if(new Date(System.currentTimeMillis()).before(extragereTimp(token))){
                return true;
            }
        }
        return false;
    }





    public SecretKey secretsemnat(){
        byte[] x= Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(x);
    }


}
