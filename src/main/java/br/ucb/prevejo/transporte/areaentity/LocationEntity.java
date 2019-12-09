package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.shared.model.Location;
import br.ucb.prevejo.transporte.parada.Parada;
import lombok.Getter;

import java.util.Collection;

@Getter
public class LocationEntity extends ParadaCollectionEntity {

    private Location location;

    public LocationEntity(Location location, Collection<Parada> paradas) {
        super(paradas);
        this.location = location;
    }

}
