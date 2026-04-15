package AndroidCalendar.backend.eveniment;

import AndroidCalendar.backend.Utilizator.DTOutilizatori;
import AndroidCalendar.backend.Utilizator.JwtService;
import AndroidCalendar.backend.Utilizator.RepositoryUtilizatori;
import AndroidCalendar.backend.Utilizator.Utilizatori;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceEvenimente {
    private final RepositoryEvenimente repositoryEvenimente;
    private final RepositoryUtilizatori repositoryUtilizatori;
    private final JwtService jwtService;


    public DTOevenimente adaugareEveniment(DTOevenimente dto, Utilizatori utilizatorCurent) {

        Evenimente eveniment = new Evenimente();
        eveniment.setOra(dto.ora());
        eveniment.setData(LocalDate.parse(dto.data()));

        eveniment.setTitlu(dto.titlu());
        eveniment.setCategore(dto.categorie());
        eveniment.setLocatie(dto.locatie());

        eveniment.setUtilizator(utilizatorCurent);

        repositoryEvenimente.save(eveniment);

        return dto;
    }
    public boolean stergereEveniment( Utilizatori utilizatori, int id) {
        List<Evenimente> evenimente=repositoryEvenimente.findByUtilizatorId(utilizatori.getId());
        for(int i=0;i<evenimente.size();i++){
            if(evenimente.get(i).getId()==id){
                repositoryEvenimente.delete(evenimente.get(i));
                return true;
            }
        }
        return false;
    }


    public DTOevenimente updateEveniment(Utilizatori utilizator,int idEveniment,DTOevenimente dto){
        List<Evenimente> evenimentes=repositoryEvenimente.findByUtilizatorId(utilizator.getId());
        Evenimente eveniment=new Evenimente();
        for(int i=0;i< evenimentes.size();i++){
            if(evenimentes.get(i).getId()==idEveniment){
                eveniment=evenimentes.get(i);
                break;
            }
        }
        eveniment.setTitlu(dto.titlu());
        eveniment.setNote(dto.note());
        eveniment.setCategore(dto.categorie());
        eveniment.setData(LocalDate.parse(dto.data()));
        eveniment.setOra(dto.ora());
        eveniment.setLocatie(dto.locatie());

        repositoryEvenimente.save(eveniment);

        return dto;
    }


    public List<DTOevenimente> listaEvenimente(Utilizatori utilizatori){
        List<Evenimente> evenimentes=repositoryEvenimente.findByUtilizatorId(utilizatori.getId());
        List<DTOevenimente> list=new ArrayList<>();
        for(int i=0;i<evenimentes.size();i++){
            String data=evenimentes.get(i).getData().toString();
            DTOevenimente dtOevenimente=new DTOevenimente(evenimentes.get(i).getId(), evenimentes.get(i).getTitlu(),data,evenimentes.get(i).getOra(),evenimentes.get(i).getLocatie(),evenimentes.get(i).getCategore(),evenimentes.get(i).getNote());
            list.add(dtOevenimente);
        }
        return list;
    }

    public List<Evenimente> afisareTot(){
        List<Evenimente> ex=repositoryEvenimente.findAll();
        return ex;
    }




}
