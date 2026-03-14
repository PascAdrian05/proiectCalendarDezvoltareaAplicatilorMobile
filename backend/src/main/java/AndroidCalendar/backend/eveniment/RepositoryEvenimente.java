package AndroidCalendar.backend.eveniment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryEvenimente extends JpaRepository<Evenimente,Integer> {
    List<Evenimente> findByUtilizatorId(Integer utilizatorId);
}
