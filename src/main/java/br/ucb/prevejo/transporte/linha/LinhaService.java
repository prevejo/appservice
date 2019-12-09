package br.ucb.prevejo.transporte.linha;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LinhaService {

    private final LinhaRepository repository;

    public LinhaService(LinhaRepository repository) {
        this.repository = repository;
    }

    public Collection<Linha> obterLinhas() {
        return repository.findAll();
    }

}
