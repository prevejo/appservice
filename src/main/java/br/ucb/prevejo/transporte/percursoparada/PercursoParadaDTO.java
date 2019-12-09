package br.ucb.prevejo.transporte.percursoparada;

import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;

public interface PercursoParadaDTO {

    PercursoDTO getPercurso();
    ParadaDTO getParada();
    Integer getSequencial();

}
