package br.ucb.prevejo.comunidade.informativo;

import br.ucb.prevejo.comunidade.informativo.provider.News;
import br.ucb.prevejo.comunidade.informativo.provider.NewsProvider;
import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.core.cache.PassiveCacheContent;
import br.ucb.prevejo.shared.exceptions.CacheException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class InformativoCacheContent implements PassiveCacheContent<String, InformativoHistory> {

	public static final String CACHE_KEY = "informativos";
	private static final String SEARCH_QUERY = "transporte-brasília";

	private InformativoHistory history;
	private final NewsProvider provider;

	public InformativoCacheContent(NewsProvider provider) {
		this.provider = provider;
	}

	public String getKey() {
		return CACHE_KEY; 
	}

	public int getCacheSeconds() {
		return 3600 * 24;
	}

	public InformativoHistory getContent() {
		return history;
	}

	public void refresh() throws CacheException {
		try {
			if (history == null) {
				history = new InformativoHistory(service().obterUltimos());
			}

			Collection<News> news = this.provider.searchInLastDay(SEARCH_QUERY);
			Informativo last = history.last().orElse(null);

			if (last != null) {
				news = news.stream().filter(n -> n.getPublishedDate().isAfter(last.getDtPublicacao())).collect(Collectors.toList());
			}

			news.stream()
					.sorted(Comparator.comparing(News::getPublishedDate).reversed())
					.findFirst()
					.map(n -> service().registrar(n))
					.ifPresent(informativo -> {
						history.push(informativo);
					});
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CacheException(e.getMessage());
		}
	}

	private InformativoService service() {
		return ContextProvider.getBean(InformativoService.class);
	}

}
