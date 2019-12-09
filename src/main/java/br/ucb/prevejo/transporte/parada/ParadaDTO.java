package br.ucb.prevejo.transporte.parada;

import org.locationtech.jts.geom.Point;

public interface ParadaDTO {

    Integer getId();
    String getCod();
    Point getGeo();
}
