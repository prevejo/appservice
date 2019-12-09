package br.ucb.prevejo.comunidade.topico;

import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TopicoCacheContent implements PassiveCacheContent<String, Collection<TopicoDTO>> {

	public static final String CACHE_KEY = "topicos";

	private Collection<TopicoDTO> topicos;

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 3600 * 24;
	}

	public Collection<TopicoDTO> getContent() {
		return topicos;
	}

	public void refresh() throws CacheException {
		try {
			this.topicos = service().obter();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CacheException(e.getMessage());
		}
	}

	private TopicoService service() {
		return ContextProvider.getBean(TopicoService.class);
	}

}
