package br.ucb.prevejo.shared.model;

import lombok.Getter;

@Getter
public class LocationViewPort {

    private Location northEast;
    private Location southWest;

    private LocationViewPort(Location northEast, Location southWest) {
        this.northEast = northEast;
        this.southWest = southWest;
    }

    public static LocationViewPort build(Location northEast, Location southWest) {
        return new LocationViewPort(northEast, southWest);
    }

}
