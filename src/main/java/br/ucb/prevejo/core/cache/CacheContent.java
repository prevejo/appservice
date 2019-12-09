package br.ucb.prevejo.core.cache;

import br.ucb.prevejo.shared.exceptions.CacheException;

import java.io.Serializable;

/**
 * Prescreve a interface de um conteúdo de cache.
 * @param <T> Tipo do conteúdo.
 */
public interface CacheContent<K, T> extends Serializable {
	public K getKey();
	public int getCacheSeconds();
	public T getContent();
	public void refresh() throws CacheException;
}
