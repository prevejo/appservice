package br.ucb.prevejo.comunidade.topico;

import br.ucb.prevejo.comunidade.comentario.ComentarioService;
import br.ucb.prevejo.core.cache.PassiveCacheService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TopicoService {
    private static final int TOPICO_ATUALIZADO_MAX_COMENTS = 5;

    private final TopicoRepository repository;
    private final ComentarioService comentarioService;
    private final PassiveCacheService<?, Collection<TopicoAtualizado>> cacheService;

    public TopicoService(TopicoRepository repository, ComentarioService comentarioService, PassiveCacheService<?, Collection<TopicoAtualizado>> cacheService) {
        this.repository = repository;
        this.comentarioService = comentarioService;
        this.cacheService = cacheService;
    }

    public Optional<Topico> obterById(int id) {
        return repository.findById(id);
    }

    public Collection<TopicoDTO> obter() {
        return repository.findAllDTO();
    }

    public Collection<TopicoDTO> obterFromCache() {
        return obterTopicoAtualizadosFromCache()
                .stream().map(topicoAtualizado -> topicoAtualizado.getTopico())
                .collect(Collectors.toList());
    }

    public TopicoAtualizado obterTopicoAtualizado(TopicoDTO topico) {
        return new TopicoAtualizado(topico, comentarioService.obterUltimos(topico, TOPICO_ATUALIZADO_MAX_COMENTS));
    }

    public Collection<TopicoAtualizado> obterTopicoAtualizadosFromCache() {
        return cacheService.getContentByClass(TopicoAtualizadoCacheContent.class)
                .orElseThrow(() -> new RuntimeException("Cache não inicializado"));
    }

    public Optional<TopicoAtualizado> obterTopicoAtualizadoById(int id) {
        return obterTopicoAtualizadosFromCache().stream().filter(t -> t.getTopico().getId().equals(id)).findFirst();
    }

    public void atualizarTopico(TopicoAtualizado topico) {
        topico.setUltimosComentarios(comentarioService.obterUltimos(topico.getTopico(), TOPICO_ATUALIZADO_MAX_COMENTS));
    }

    public Collection<TopicoAtualizado> obterTopicosAtualizados() {
        return obter().stream().map(topico -> obterTopicoAtualizado(topico)).collect(Collectors.toList());
    }

}
