package br.ucb.prevejo.transporte.areaintegracao;

import org.locationtech.jts.geom.MultiPolygon;

public interface AreaIntegracaoDTO {

    public Integer getId();
    public String getDescricao();
    public MultiPolygon getGeo();

}
