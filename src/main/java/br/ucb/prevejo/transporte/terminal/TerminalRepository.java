package br.ucb.prevejo.transporte.terminal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface TerminalRepository extends JpaRepository<Terminal, Integer> {

    Optional<Terminal> findByCod(String cod);

}
