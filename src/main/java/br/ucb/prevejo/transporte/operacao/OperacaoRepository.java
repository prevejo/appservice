package br.ucb.prevejo.transporte.operacao;

import br.ucb.prevejo.transporte.percurso.Percurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface OperacaoRepository extends JpaRepository<Operacao, Integer> {

    List<Operacao> findByPercurso(Percurso percurso);

}
