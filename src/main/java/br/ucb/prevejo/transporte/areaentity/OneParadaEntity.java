package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.shared.model.Location;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaFetch;

import java.util.Arrays;

public class OneParadaEntity extends ParadaCollectionEntity implements ExpandableAreaEntity {
    private static final int EXPANDED_RANGE = 500;
    private static final int MAX_RANGE = EXPANDED_RANGE * 40;

    private Parada parada;

    private int currentRange;

    public OneParadaEntity(Parada parada) {
        super(Arrays.asList(parada));
        this.parada = parada;
    }

    public Parada getParada() {
        return parada;
    }

    @Override
    public boolean expand(ParadaFetch paradaFetch) {
        currentRange += EXPANDED_RANGE;
        if (currentRange < MAX_RANGE) {
            super.addParadas(paradaFetch.fetchInRange(Location.build(parada.getGeo()), currentRange));
        } else {
            return false;
        }

        return true;
    }

}
