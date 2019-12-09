package br.ucb.prevejo.comunidade.informativo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InformativoRepository extends JpaRepository<Informativo, Integer> {

    @Query("SELECT i FROM Informativo i ORDER BY i.dtPublicacao DESC")
    List<Informativo> findInformativosLast(Pageable page);

    @Query("SELECT i FROM Informativo i WHERE i.dtPublicacao > :dtPublicacao ORDER BY i.dtPublicacao DESC")
    List<Informativo> findInformativosLast(@Param("dtPublicacao") LocalDateTime dtPublicacao, Pageable page);

}
