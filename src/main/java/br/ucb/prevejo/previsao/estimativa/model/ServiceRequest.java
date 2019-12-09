package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ServiceRequest {

    private String numero;
    private String sentido;
    private String parada;
    private Collection<LocatedEntity> veiculos;

}
