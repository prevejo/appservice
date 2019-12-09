package br.ucb.prevejo.transporte.trajeto;

import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class TrechoPercurso {

    private Collection<ParadaDTO> embarques;
    private Collection<ParadaDTO> desembarques;
    private PercursoDTO percurso;

}
