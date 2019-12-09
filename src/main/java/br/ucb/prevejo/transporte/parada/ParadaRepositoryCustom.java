package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.shared.model.Location;

import java.util.List;

public interface ParadaRepositoryCustom {

    public List<Parada> findParadasProximas(Location location, int limite);
    public List<Parada> findParadasInRange(Location location, int range);

}
