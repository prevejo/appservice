package br.ucb.prevejo.transporte.percurso;

import org.locationtech.jts.geom.LineString;

public interface PercursoGeoDTO {

    Integer getId();
    EnumSentido getSentido();
    LineString getGeo();
    String getOrigem();
    String getDestino();

}
