package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.percurso.Percurso;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ServiceRequest {

    private Percurso percurso;
    private Parada parada;
    private Collection<LocatedEntity> veiculos;

}
