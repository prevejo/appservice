package br.ucb.prevejo.transporte.terminal;

import br.ucb.prevejo.transporte.percurso.Percurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TerminalRepository extends JpaRepository<Terminal, Integer> {

}
