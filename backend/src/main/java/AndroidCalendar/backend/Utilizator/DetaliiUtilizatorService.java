package AndroidCalendar.backend.Utilizator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DetaliiUtilizatorService implements UserDetailsService {

    private final RepositoryUtilizatori repositoryUtilizatori;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return repositoryUtilizatori.findByNume(username).orElseThrow(()->new UsernameNotFoundException("Nu exista"));
    }
}
