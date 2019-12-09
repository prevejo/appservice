package br.ucb.prevejo.core.cache;

import br.ucb.prevejo.shared.exceptions.CacheException;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mantém um conteúdo com o instante da sua última atualização.
 */
public class CacheState<K, V> {
	private Date lastRefresh;
	private CacheContent<K, V> cacheContent;
	private Lock lock = new ReentrantLock();
	
	public CacheContent<K, V> getCacheContent() {
		return cacheContent;
	}
	
	public void setCacheContent(CacheContent<K, V> cc) {
		this.cacheContent = cc;
	}


	public Optional<V> tryRefresh() {
		if (lock.tryLock()) {
			try {
				if (isTimeToRefresh()) {
					refresh();

					return Optional.of(getCacheContent().getContent());
				}
			} finally {
				lock.unlock();
			}
		}

		return Optional.empty();
	}

	/**
	 * Atualiza o conteúdo do chave.
	 * @throws CacheException
	 */
	public void refresh() throws CacheException {
		try {
			cacheContent.refresh();
			lastRefresh = new Date();
		} catch(CacheException e) {
			lastRefresh = null;
			throw e;
		}
	}
	
	/**
	 * Verifica se já decorreu tempo suficiente para atualizar o conteúdo. 
	 * @return True, conteúdo precisa ser atualizado; false, caso contrário.
	 */
	public boolean isTimeToRefresh() {
		if (lastRefresh != null) {
			long seconds = getSecondsBetween(lastRefresh, new Date());

			return cacheContent.getCacheSeconds() <= seconds;
		}
		
		return true;
	}
	
	/**
	 * Obtem a quantidade de segundos entre duas datas.
	 * @param dt1 Primeira data.
	 * @param dt2 Segunda data.
	 * @return Quantidade de segundos.
	 */
	private long getSecondsBetween(Date dt1, Date dt2) {
		long seconds = (dt1.getTime()-dt2.getTime()) / 1000;
		
		if (seconds < 0) {
			seconds = seconds * (-1);
		}
		
		return seconds;
	}

}
