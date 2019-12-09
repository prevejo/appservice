package br.ucb.prevejo.transporte.percursoparada;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PercursoParadaRepository extends JpaRepository<PercursoParada, PercursoParadaID>, JpaSpecificationExecutor, PercursoParadaRepositoryCustom {
}
