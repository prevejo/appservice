package br.ucb.prevejo.comunidade.topico;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/topico")
public class TopicoController {

    private final TopicoService service;

    public TopicoController(TopicoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<TopicoDTO>> obterTopicos() {
        return new ResponseEntity<>(service.obterFromCache(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/atualizados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<TopicoAtualizado>> obterTopicosAtualizados() {
        return new ResponseEntity<>(service.obterTopicoAtualizadosFromCache(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/atualizados/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TopicoAtualizado> obterTopicoAtualizado(@PathVariable("id") Integer id) {
        return service.obterTopicoAtualizadoById(id)
                .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

}
