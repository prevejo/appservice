package br.ucb.prevejo.comunidade.comentario;

import br.ucb.prevejo.comunidade.topico.TopicoDTO;
import br.ucb.prevejo.comunidade.topico.TopicoService;
import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.shared.util.DateAndTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    private final ComentarioRepository repository;
    private final ApplicationEventPublisher publisher;

    public ComentarioService(ComentarioRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Collection<ComentarioDTO> obterUltimos(TopicoDTO topico, int total) {
        return repository.findComentariosLast(topico.getId(), PageRequest.of(0, total))
                .stream().map(c -> c.toDTO()).collect(Collectors.toList());
    }

    public Collection<ComentarioDTO> search(int topicoId, String searchValue, int total) {
        return repository.findComentariosSearch(topicoId, "%"+searchValue+"%", PageRequest.of(0, total))
                .stream().map(c -> c.toDTO()).collect(Collectors.toList());
    }

    public Optional<ComentarioDTO> incrementarRelevancia(int comentarioId) {
        return repository.findById(comentarioId)
            .map(comentario -> {
                comentario.setRelevancia(comentario.getRelevancia() + 1);
                repository.save(comentario);
                publisher.publishEvent(new ComentarioAlteradoEvent(this, comentario));
                return comentario.toDTO();
            });
    }

    public Optional<Comentario> registrar(NovoComentarioDTO novoComentario) {
        TopicoService topicoService = ContextProvider.getBean(TopicoService.class);

        return Optional.ofNullable(topicoService.obterById(novoComentario.getTopicoId())
                .map(topico -> new Comentario(novoComentario.getAssunto(), novoComentario.getComentario(), DateAndTime.now(), topico))
                .map(comentario -> registrar(comentario)).orElse(null));
    }

    public Comentario registrar(Comentario comentario) {
        comentario = repository.save(comentario);

        publisher.publishEvent(new ComentarioAlteradoEvent(this, comentario));

        return comentario;
    }

}
