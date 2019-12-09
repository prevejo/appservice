package br.ucb.prevejo.comunidade.topico;

import br.ucb.prevejo.comunidade.comentario.ComentarioAlteradoEvent;
import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class TopicoAtualizadoCacheContent implements PassiveCacheContent<String, Collection<TopicoAtualizado>>, ApplicationListener<ComentarioAlteradoEvent> {

	public static final String CACHE_KEY = "topicos-atualizados";

	private Collection<TopicoAtualizado> topicos = Collections.emptyList();

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 30 * 60;
	}

	public Collection<TopicoAtualizado> getContent() {
		return topicos;
	}

	public void refresh() throws CacheException {
		try {
			this.topicos = service().obterTopicosAtualizados();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CacheException(e.getMessage());
		}
	}

	private TopicoService service() {
		return ContextProvider.getBean(TopicoService.class);
	}

	@Override
	public void onApplicationEvent(ComentarioAlteradoEvent event) {
		this.topicos.stream()
				.filter(topico -> topico.getTopico().getId().equals(event.getComentario().getTopico().getId()))
				.forEach(topico -> service().atualizarTopico(topico));
	}

}
