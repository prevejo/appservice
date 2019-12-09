package br.ucb.prevejo.transporte.parada;

import static br.ucb.prevejo.core.resources.SqlResources.*;
import br.ucb.prevejo.shared.model.Location;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class ParadaRepositoryImpl implements ParadaRepositoryCustom {

    @Autowired
    private EntityManager manager;

    @Override
    public List<Parada> findParadasProximas(Location location, int limite) {
        return manager.createNativeQuery(find(PARADAS_BY_DISTANCIA), Parada.class)
                .setParameter(1, location.getLng())
                .setParameter(2, location.getLat())
                .setParameter(3, limite)
                .getResultList();
    }

    @Override
    public List<Parada> findParadasInRange(Location location, int range) {
        return manager.createNativeQuery(find(PARADAS_IN_RANGE), Parada.class)
                .setParameter(1, location.getLng())
                .setParameter(2, location.getLat())
                .setParameter(3, range)
                .getResultList();
    }

}
