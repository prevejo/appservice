package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EstimativaRequest {

    @Getter
    private Percurso percurso;

    @EqualsAndHashCode.Include
    @Getter
    private Parada paradaEmbarque;
    @EqualsAndHashCode.Include
    private Integer percursoId;

    public EstimativaRequest(Percurso percurso, Parada paradaEmbarque) {
        this.percursoId = percurso.getId();
        this.percurso = percurso;
        this.paradaEmbarque = paradaEmbarque;
    }

}
