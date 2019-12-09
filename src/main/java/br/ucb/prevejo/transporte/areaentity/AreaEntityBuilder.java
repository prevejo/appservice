package br.ucb.prevejo.transporte.areaentity;


public interface AreaEntityBuilder {

    public AreaEntity buildForLocation(String data);
    public AreaEntity buildForParadaGroup(String data);
    public AreaEntity buildForParada(String data);

}
