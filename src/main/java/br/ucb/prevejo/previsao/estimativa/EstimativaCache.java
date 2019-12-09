package br.ucb.prevejo.previsao.estimativa;

import br.ucb.prevejo.previsao.estimativa.model.EstimativaRequest;
import br.ucb.prevejo.previsao.estimativa.model.HistoricoOperacao;
import br.ucb.prevejo.shared.util.DateAndTime;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EstimativaCache {

    private final Map<CacheEntry, HistoricoOperacao> map = new HashMap<>();
    private final CleanWorker worker = new CleanWorker(map, Duration.of(60, ChronoUnit.SECONDS));

    public void add(EstimativaRequest request, HistoricoOperacao historicoOperacao) {
        synchronized (map) {
            map.put(new CacheEntry(request), historicoOperacao);

            if (!worker.isRunning()) {
                worker.start();
            }
        }
    }

    public Optional<HistoricoOperacao> getHistorico(EstimativaRequest request) {
        synchronized (map) {
            return map.keySet().stream()
                    .filter(ce -> ce.hasKey(request))
                    .findFirst()
                    .map(ce -> {
                        ce.updateTime();
                        return map.get(ce);
                    });
        }
    }

    public void stopRefreshWorkers() {
        worker.stop();
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private class CacheEntry<K> {

        @EqualsAndHashCode.Include
        private K key;
        private LocalDateTime lastUpdate;

        public CacheEntry(K key) {
            this.key = key;
            updateTime();
        }

        public boolean isExpired(Duration duration) {
            return !DateAndTime.isBehind(lastUpdate, duration);
        }

        public boolean hasKey(K key) {
            return this.key.equals(key);
        }

        public void updateTime() {
            this.lastUpdate = LocalDateTime.now();
        }
    }

    private class CleanWorker implements Runnable {
        private boolean running;
        private Map<CacheEntry, ?> map;
        private Duration duration;

        public CleanWorker(Map<CacheEntry, ?> map, Duration duration) {
            this.map = map;
            this.duration = duration;
        }

        public void start() {
            running = true;
            new Thread(this).start();
        }

        public void stop() {
            running = false;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    synchronized (map) {
                        map.keySet().stream()
                            .filter(ce -> ce.isExpired(duration))
                            .collect(Collectors.toList())
                            .forEach(ce -> {
                                System.out.println("<-> Expire: " + ce);
                                map.remove(ce);
                            });
                    }

                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                 running = false;
            }
        }
    }

}
