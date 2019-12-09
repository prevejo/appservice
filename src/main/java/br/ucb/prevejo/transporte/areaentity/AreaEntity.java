package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.transporte.parada.ParadaDTO;

import java.util.Collection;

public interface AreaEntity {

    Collection<ParadaDTO> paradas();
}
