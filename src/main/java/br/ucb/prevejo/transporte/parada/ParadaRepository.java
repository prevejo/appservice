package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.transporte.percurso.Percurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ParadaRepository extends JpaRepository<Parada, Integer>, ParadaRepositoryCustom {

    Optional<Parada> findByCod(String cod);

    List<Parada> findByCodIn(Collection<String> cod);

    @Query(value = "SELECT p.paradas FROM Percurso p WHERE p = :percurso")
    List<Parada> findParadasByPercurso(@Param("percurso") Percurso percurso);

}
