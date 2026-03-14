package AndroidCalendar.backend.Utilizator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryUtilizatori extends JpaRepository<Utilizatori,Integer> {
    Optional<Utilizatori> findByNume(String nume);
}
