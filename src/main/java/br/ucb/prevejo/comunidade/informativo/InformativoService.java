package br.ucb.prevejo.comunidade.informativo;

import br.ucb.prevejo.comunidade.informativo.provider.News;
import br.ucb.prevejo.core.cache.PassiveCacheService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InformativoService {

    private static final int MAX_LAST_INFORMATIVOS = 3;

    private final InformativoRepository repository;
    private final PassiveCacheService<?, InformativoHistory> cacheService;

    public InformativoService(InformativoRepository repository, PassiveCacheService<?, InformativoHistory> cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    public Optional<Informativo> obterById(Integer id) {
        return repository.findById(id);
    }

    public Collection<Informativo> obterUltimos() {
        return repository.findInformativosLast(PageRequest.of(0, MAX_LAST_INFORMATIVOS));
    }

    public Collection<InformativoDTO> obterFeed() {
        return history().retriveAll().stream().map(i -> i.toDTO()).collect(Collectors.toList());
    }

    public Collection<InformativoDTO> obterFeed(LocalDateTime lastDate) {
        return history().retrive(lastDate).stream().map(i -> i.toDTO()).collect(Collectors.toList());
    }

    public Informativo registrar(News news) {
        Informativo informativo = new Informativo(news.getTitle(), news.getDescription(), news.getPublishedDate(), news.getUrl());

        return repository.save(informativo);
    }

    private InformativoHistory history() {
        return cacheService.getContentByClass(InformativoCacheContent.class)
                .orElseThrow(() -> new RuntimeException("Cache não inicializado"));
    }
}
