package br.ucb.prevejo.core.cache;

import br.ucb.prevejo.shared.exceptions.CacheException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementa um provedor de cache com atualização passiva.
 * @param <K> Tipo da chave dos conteúdos de cache.
 * @param <V> Tipo dos conteúdos que sofrerão cache.
 */
public class PassiveCacheProvider<K, V> implements CacheProvider<K, V> {
	
	private Map<K, CacheState<K, V>> map = new HashMap<>();
	private boolean returnOnException;
	
	/**
	 * Define se o conteúdo será retornado mesmo no caso de exeção ao atualizar o cache.
	 * @param returnOnException True, retorno no caso de exeção; false, exeções serão propagadas.
	 */
	public void setReturnOnException(boolean returnOnException) {
		this.returnOnException = returnOnException;
	}
	
	/**
	 * Obtem um conteúdo em cache para uma dada chave.
	 * @param key Chave.
	 * @return Conteúdo.
	 * @throws CacheException
	 */
	public V getContent(K key) throws CacheException {
		CacheState<K, V> cacheState = getCacheState(key);
		
		if (cacheState != null) {
			return extractContent(cacheState);			
		}
		
		throw new CacheException("Chave não possui cache content associado!");
	}

	public Optional<V> getContentByClass(Class<? extends CacheContent<?, V>> classe) {
		return map.values().stream()
				.filter(cs -> classe.isInstance(cs.getCacheContent()))
				.map(cs -> extractContent(cs))
				.findFirst();
	}
	
	/**
	 * Adiciona um conteúdo ao cache.
	 * @param cacheContent CacheContent.
	 */
	public synchronized void addCacheContent(CacheContent<K, V> cacheContent) {
		CacheState<K, V> state = new CacheState<>();
		state.setCacheContent(cacheContent);
		
		map.put(cacheContent.getKey(), state);
	}

	/**
	 * Obtem a coleção de conteúdos em cache.
	 */
	@Override
	public synchronized Collection<V> getContents() throws CacheException {
		return map.values().parallelStream().map(cs -> extractContent(cs)).collect(Collectors.toList());
	}
	
	/**
	 * Obtem o CacheState de associado a uma chave no mapa.
	 * @param key Chave.
	 * @return CacheState.
	 */
	private synchronized CacheState<K, V> getCacheState(K key) {
		return map.get(key);
	}
	
	/**
	 * Obtem o conteúdo de um cache state.
	 * @param cacheState CacheState.
	 * @return
	 */
	private V extractContent(CacheState<K, V> cacheState) {
		synchronized (cacheState) {
			if (cacheState.isTimeToRefresh()) {
				try {
					cacheState.refresh();
				} catch(CacheException e) {
					if (!returnOnException) {
						throw e;
					} else {
						e.printStackTrace();
					}
				}
			}
			
			return cacheState.getCacheContent().getContent();	
		}
	}

}
