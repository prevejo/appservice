package br.ucb.prevejo.comunidade.comentario;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    @Query("SELECT c FROM Comentario c WHERE c.topico.id = :idTopico ORDER BY c.dtPublicacao DESC")
    List<Comentario> findComentariosLast(@Param("idTopico") Integer idTopico, Pageable page);

    @Query("SELECT c FROM Comentario c " +
            "WHERE c.topico.id = :idTopico " +
                "and (lower(c.assunto) LIKE lower(:searchValue) " +
                "or lower(c.comentario) LIKE lower(:searchValue)) " +
            "ORDER BY c.dtPublicacao DESC")
    List<Comentario> findComentariosSearch(@Param("idTopico") Integer idTopico, @Param("searchValue") String searchValue, Pageable page);

}
