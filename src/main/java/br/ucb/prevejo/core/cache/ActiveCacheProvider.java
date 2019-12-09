package br.ucb.prevejo.core.cache;

import br.ucb.prevejo.shared.exceptions.CacheException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementa um provedor de cache de atualização ativa.
 * @param <K> Tipo da chave dos conteúdos de cache.
 * @param <V> Tipo dos conteúdos que sofrerão cache.
 */
public class ActiveCacheProvider<K, V> implements CacheProvider<K, V> {

	private Map<K, CacheState<K, V>> map = new HashMap<>();
	private RefreshWorker worker = new RefreshWorker(() -> getStates());


	public void setRefreshListener(Consumer<V> refreshListener) {
		worker.setRefreshListener(refreshListener);
	}

	public void startRefresh() {
		worker.start();
	}

	public boolean isRunning() {
		return worker.isRunning();
	}

	public void stopRefresh() {
		worker.stop();
	}


	@Override
	public V getContent(K key) throws CacheException {
		CacheState<K, V> cacheState = getCacheState(key);
		
		if (cacheState != null) {
			return extractContent(cacheState);			
		}
		
		throw new CacheException("Chave não possui cache content associado!");
	}

	@Override
	public void addCacheContent(CacheContent<K, V> cacheContent) {
		CacheState<K, V> state = new CacheState<>();
		state.setCacheContent(cacheContent);
		
		map.put(cacheContent.getKey(), state);
	}

	@Override
	public Collection<V> getContents() throws CacheException {
		return map.values().parallelStream().map(cs -> extractContent(cs)).filter(v -> v != null).collect(Collectors.toList());
	}


	private CacheState<K, V> getCacheState(K key) {
		return map.get(key);
	}

	private V extractContent(CacheState<K, V> cacheState) {
			return cacheState.getCacheContent().getContent();
	}

	private synchronized Collection<CacheState<K, V>> getStates() {
		return map.values().stream().collect(Collectors.toList());
	}


	/**
	 * Worker que atualiza o cache.
	 */
	private class RefreshWorker implements Runnable {

		private boolean running;
		private boolean stopped;
		private Thread thread;
		private Supplier<Collection<CacheState<K, V>>> statesSupplier;
		private Consumer<V> refreshListener;
		
		public RefreshWorker(Supplier<Collection<CacheState<K, V>>> statesSupplier) {
			this.statesSupplier = statesSupplier;
		}
		
		public void setRefreshListener(Consumer<V> refreshListener) {
			this.refreshListener = refreshListener;
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public boolean isStopped() {
			return stopped;
		}
		
		public void start() {
			this.thread = new Thread(this);
			this.thread.start();
		}
		
		public void stop() {
			this.stopped = true;
		}
		
		@Override
		public void run() {
			this.running = true;
			this.stopped = false;
			
			try {
				while (!isStopped()) {
					try {
						this.statesSupplier.get().stream().map(state -> new Thread(() -> updateState(state))).forEach(t -> t.start());
					} catch(Throwable e) {
						e.printStackTrace();
					}

					Thread.sleep(1000);
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			} finally {
				this.running = false;
			}
		}

		private void updateState(CacheState<K, V> state) {
			try {
				state.tryRefresh().ifPresent(content -> {
					if (this.refreshListener != null) {
						this.refreshListener.accept(content);
					}
				});
			} catch(Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
