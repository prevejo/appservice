package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.shared.model.Location;

import java.util.Collection;

public interface ParadaFetch {

    public Collection<Parada> fetchInRange(Location location, int radiusDistance);

}
