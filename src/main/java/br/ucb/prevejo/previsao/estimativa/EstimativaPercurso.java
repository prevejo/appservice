package br.ucb.prevejo.previsao.estimativa;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class EstimativaPercurso {

    private PercursoDTO percurso;
    private LocatedEntity endPoint;
    private Collection<EstimativaChegada> chegadas;

}
