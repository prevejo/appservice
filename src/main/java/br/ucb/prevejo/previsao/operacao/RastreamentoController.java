package br.ucb.prevejo.previsao.operacao;

import br.ucb.prevejo.previsao.operacao.veiculo.VeiculoOperacao;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.PercursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/rastreamento")
public class RastreamentoController {

    private final OperacaoExecucaoService service;
    private final PercursoService percursoService;

    public RastreamentoController(OperacaoExecucaoService service, PercursoService percursoService) {
        this.service = service;
        this.percursoService = percursoService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/operacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Operacao>> operacoes() {
        return new ResponseEntity<>(service.obterOperacoes(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/veiculos/percurso/{numLinha:.+}/{sentido}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<VeiculoOperacao>> veiculosEmOperacao(@PathVariable String numLinha, @PathVariable EnumSentido sentido) {
        Collection<VeiculoOperacao> veiculos = percursoService.obterPercurso(numLinha, sentido)
                .map(percurso -> service.obterVeiculosEmOperacao(percurso))
                .orElse(Collections.emptyList());

        return new ResponseEntity<>(veiculos, HttpStatus.OK);
    }

}
