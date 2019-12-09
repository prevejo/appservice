package br.ucb.prevejo.transporte.grafo;

import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import org.springframework.stereotype.Component;

@Component
public class GrafoTransporteCacheContent implements PassiveCacheContent<String, GrafoTransporte> {
	private static final long serialVersionUID = 1L;
	public static final String CACHE_KEY = "grafo-transporte-inicial";

	private GrafoTransporte grafo;

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 3600 * 24;
	}

	public GrafoTransporte getContent() {
		return grafo;
	}

	public void refresh() throws CacheException {
		try {
			this.grafo = ContextProvider.getBean(GrafoTransporteService.class).build();
		} catch (Throwable e) {
			throw new CacheException(e.getMessage());
		}
	}

}
