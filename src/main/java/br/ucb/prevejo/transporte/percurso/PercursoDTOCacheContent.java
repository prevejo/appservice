package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PercursoDTOCacheContent implements PassiveCacheContent<String, Collection<PercursoDTO>> {
	private static final long serialVersionUID = 1L;
	public static final String CACHE_KEY = "percursos-dto";

	private Collection<PercursoDTO> percursos;

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 3600 * 24;
	}

	public Collection<PercursoDTO> getContent() {
		return percursos;
	}

	public void refresh() throws CacheException {
		try {
			this.percursos = ContextProvider.getBean(PercursoService.class).obterPercursosDTO();
		} catch (Throwable e) {
			throw new CacheException(e.getMessage());
		}
	}

}
