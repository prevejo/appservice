package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.core.cache.PassiveCacheService;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.model.Location;
import br.ucb.prevejo.transporte.percurso.Percurso;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ParadaService implements ParadaFetch {

    private final ParadaRepository repository;
    private final PassiveCacheService<?, FeatureCollection> cacheService;

    public ParadaService(ParadaRepository repository, PassiveCacheService<?, FeatureCollection> cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    public Collection<Parada> obterParadas() {
        return repository.findAll();
    }

    public FeatureCollection obterFeatureCollection() {
        return toFeatureCollection(repository.findAll());
    }

    public FeatureCollection obterFeatureCollectionFromCache() {
        return cacheService.getContentByClass(ParadaCollectionCacheContent.class)
                .orElseThrow(() -> new RuntimeException("Cache não presente"));
    }

    public FeatureCollection obterFeatureCollection(Percurso percurso) {
        return toFeatureCollection(repository.findParadasByPercurso(percurso));
    }

    public Optional<Parada> obterPorCodigo(String codigo) {
        return repository.findByCod(codigo);
    }

    public Collection<Parada> obterPorCodigo(Collection<String> codigos) {
        if (codigos.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findByCodIn(codigos);
    }

    public Collection<Parada> obterParadasMaisProximas(Location location, int limite) {
        return repository.findParadasProximas(location, limite);
    }

    @Override
    public Collection<Parada> fetchInRange(Location location, int radiusDistance) {
        return repository.findParadasInRange(location, radiusDistance);
    }

    private FeatureCollection toFeatureCollection(Collection<Parada> paradas) {
        return FeatureCollection.<Parada>build(
                paradas,
                p -> p.getGeo(),
                p -> new HashMap<String, Object>() {{
                    put("codigo", p.getCod());
                }}
        );
    }

}
