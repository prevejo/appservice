package br.ucb.prevejo.comunidade.topico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface TopicoRepository extends JpaRepository<Topico, Integer> {

    @Query(value = "SELECT t.id as id, t.titulo as titulo FROM Topico t ")
    List<TopicoDTO> findAllDTO();

    @Query(value = "SELECT t.id as id, t.titulo as titulo FROM Topico t WHERE t.id = :id")
    Optional<TopicoDTO> findTopicoDTOById(@Param("id") int id);

}
