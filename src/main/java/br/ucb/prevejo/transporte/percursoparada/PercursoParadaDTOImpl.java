package br.ucb.prevejo.transporte.percursoparada;

import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PercursoParadaDTOImpl implements PercursoParadaDTO {

    @EqualsAndHashCode.Include
    private PercursoDTO percurso;

    @EqualsAndHashCode.Include
    private ParadaDTO parada;

    private Integer sequencial;
}
