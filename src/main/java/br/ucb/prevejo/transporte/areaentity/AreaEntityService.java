package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.shared.model.Location;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AreaEntityService implements AreaEntityBuilder {

    private final ParadaService paradaService;

    public AreaEntityService(ParadaService paradaService) {
        this.paradaService = paradaService;
    }

    public AreaEntity buildForLocation(String data) {
        String split[] = data.split("_");

        if (split.length == 2) {
            Location location = Location.build(split[0], split[1]);
            Collection<Parada> paradas = paradaService.obterParadasMaisProximas(location, 5);

            return new LocationEntity(location, paradas);
        } else {
            throw new RuntimeException("Entrada inválida");
        }
    }

    public AreaEntity buildForParadaGroup(String data) {
        String split[] = data.split("_");

        if (split.length == 0) {
            Collection<Parada> paradas = paradaService.obterPorCodigo(Arrays.asList(split).stream().collect(Collectors.toList()));

            if (paradas.isEmpty()) {
                throw new RuntimeException("Nenhuma parada encontrada");
            }

            return new ParadaCollectionEntity(paradas);
        } else {
            throw new RuntimeException("Entrada inválida");
        }
    }

    public AreaEntity buildForParada(String data) {
        Parada parada = paradaService.obterPorCodigo(data)
                .orElseThrow(() -> new RuntimeException("Parada não encontrada"));

        return new OneParadaEntity(parada);
    }

}
