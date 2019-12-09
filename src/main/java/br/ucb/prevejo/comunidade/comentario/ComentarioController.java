package br.ucb.prevejo.comunidade.comentario;

import br.ucb.prevejo.comunidade.topico.TopicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/comentario")
public class ComentarioController {

    private final ComentarioService service;
    private final TopicoService topicoService;

    public ComentarioController(ComentarioService service, TopicoService topicoService) {
        this.service = service;
        this.topicoService = topicoService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/novo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comentario> registrar(@RequestBody @Valid NovoComentarioDTO novoComentario) {
        return service.registrar(novoComentario)
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search/{topicoId}/{maxResults}/{searchValue}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComentarioDTO>> obterTopicoAtualizado(@PathVariable("topicoId") Integer topicoId, @PathVariable("maxResults") Integer maxResults, @PathVariable("searchValue") String searchValue) {
        return new ResponseEntity<>(service.search(topicoId, searchValue, maxResults), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/recents/{topicoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ComentarioDTO>> obterMaisRecentes(@PathVariable("topicoId") Integer id) {
        return this.topicoService.obterTopicoAtualizadoById(id)
                .map(topicoAtualizado -> topicoAtualizado.getUltimosComentarios())
                .map(comentarios -> new ResponseEntity<>(comentarios, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/relevancia/incrementar/{comentarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComentarioDTO> incrementarRelevancia(@PathVariable("comentarioId") Integer comentarioId) {
        return service.incrementarRelevancia(comentarioId)
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

}
