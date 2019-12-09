package br.ucb.prevejo.transporte.trajeto;

import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class Trecho {

    private AreaEntity areaEmbarque;
    private AreaEntity areaDesembarque;
    private Collection<TrechoPercurso> percursos;

}
