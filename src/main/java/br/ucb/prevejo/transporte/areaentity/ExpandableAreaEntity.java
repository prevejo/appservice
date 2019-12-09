package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.transporte.parada.ParadaFetch;

public interface ExpandableAreaEntity extends AreaEntity {

    public boolean expand(ParadaFetch fetch);

}
