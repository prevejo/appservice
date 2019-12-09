package br.ucb.prevejo.transporte.operacao;

import br.ucb.prevejo.transporte.percurso.Percurso;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

@Service
public class OperacaoService {

    private final OperacaoRepository repository;

    public OperacaoService(OperacaoRepository repository) {
        this.repository = repository;
    }

    public Collection<Operacao> obterOperacoes(Percurso percurso) {
        return new LinkedHashSet<>(repository.findByPercurso(percurso));
    }

}
