package br.ucb.prevejo.transporte.operacao;

import br.ucb.prevejo.transporte.percurso.PercursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/operacao")
public class OperacaoController {

    private final OperacaoService service;
    private final PercursoService percursoService;

    public OperacaoController(OperacaoService service, PercursoService percursoService) {
        this.service = service;
        this.percursoService = percursoService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/percurso/{percursoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Operacao>> obterOperacoes(@PathVariable("percursoId") Integer percursoId) {
        return percursoService.obterPercurso(percursoId)
                .map(p -> service.obterOperacoes(p))
                .map(collection -> new ResponseEntity<>(collection, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

}
