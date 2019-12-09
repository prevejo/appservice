package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.transporte.grafo.GrafoTransporte;
import br.ucb.prevejo.transporte.grafo.GrafoTransporteService;
import org.springframework.stereotype.Component;

@Component
public class ParadaCollectionCacheContent implements PassiveCacheContent<String, FeatureCollection> {
	private static final long serialVersionUID = 1L;
	public static final String CACHE_KEY = "paradas-feature-collection";

	private FeatureCollection collection;

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 3600 * 24;
	}

	public FeatureCollection getContent() {
		return collection;
	}

	public void refresh() throws CacheException {
		try {
			this.collection = ContextProvider.getBean(ParadaService.class).obterFeatureCollection();
		} catch (Throwable e) {
			throw new CacheException(e.getMessage());
		}
	}

}
