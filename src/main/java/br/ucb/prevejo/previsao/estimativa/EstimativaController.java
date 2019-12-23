package br.ucb.prevejo.previsao.estimativa;

import br.ucb.prevejo.shared.intefaces.HttpClient;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/estimativa")
public class EstimativaController {

    private final EstimativaService service;
    private final HttpClient httpClient;

    public EstimativaController(EstimativaService service, HttpClient httpClient) {
        this.service = service;
        this.httpClient = httpClient;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/percurso/{numLinha:.+}/{sentido}/{codParada}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String estimativaByLinha(@PathVariable String numLinha, @PathVariable EnumSentido sentido, @PathVariable String codParada) {
        return service.estimar(numLinha, sentido, codParada);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/percurso/{percursoId}/{codParada}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String estimativaByLinha(@PathVariable Integer percursoId, @PathVariable String codParada) {
        return service.estimar(percursoId, codParada);
    }

}
