package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.transporte.parada.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface PercursoRepository extends JpaRepository<Percurso, Integer>, JpaSpecificationExecutor {

    List<PercursoGeoDTO> findByIdIn(Collection<Integer> ids);

    @Query(value = "SELECT p FROM Percurso p JOIN FETCH p.linha l " +
            "WHERE l = (select ll from Percurso pp join pp.linha ll where pp.id = :percursoId)")
    List<Percurso> findPercursosByPercursoId(@Param("percursoId") int percursoId);

    @Query(value = "SELECT p.id as id, p.sentido as sentido, p.linha as linha, p.origem as origem, p.destino as destino FROM Percurso p ")
    List<PercursoDTO> findAllDTO();

    @Query(value = "SELECT p.id as id, p.sentido as sentido, p.linha as linha, p.origem as origem, p.destino as destino FROM Percurso p " +
            "WHERE p in (select pp.percurso from PercursoParada pp where pp.parada = :parada)")
    List<PercursoDTO> findAllByParada(@Param("parada") Parada parada);

    @Query(value = "SELECT p.id as id, p.sentido as sentido, p.linha as linha, p.origem as origem, p.destino as destino FROM Percurso p " +
            "WHERE UPPER(p.linha.descricao) LIKE CONCAT('%', CONCAT(UPPER(:desc), '%')) OR p.linha.numero LIKE CONCAT('%', CONCAT(:desc, '%')) ")
    List<PercursoDTO> findAllByDescricao(@Param("desc") String descricao);

    @Query(value = "SELECT p FROM Percurso p WHERE p.linha.numero = :numLinha and p.sentido = :sentido")
    Optional<Percurso> findPercursoByLinhaAndSentido(@Param("numLinha") String numLinha, @Param("sentido") EnumSentido sentido);

    @Query(value = "SELECT p.id as id, p.sentido as sentido, p.linha as linha, p.origem as origem, p.destino as destino FROM Percurso p " +
            "WHERE p.id = :id")
    Optional<PercursoDTO> findPercursoDTOById(@Param("id") int id);

    @Query(value = "SELECT p.id as id, p.sentido as sentido, p.linha as linha, p.origem as origem, p.destino as destino FROM Percurso p " +
            "WHERE p.linha.numero = :numLinha and p.sentido = :sentido")
    Optional<PercursoDTO> findPercursoDTOByLinhaAndSentido(@Param("numLinha") String numLinha, @Param("sentido") EnumSentido sentido);

}
