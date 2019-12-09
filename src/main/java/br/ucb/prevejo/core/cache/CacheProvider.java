package br.ucb.prevejo.core.cache;

import br.ucb.prevejo.shared.exceptions.CacheException;

import java.util.Collection;

public interface CacheProvider<K, V> {
	public V getContent(K key) throws CacheException;
	public void addCacheContent(CacheContent<K, V> cacheContent);
	public Collection<V> getContents() throws CacheException;
}
