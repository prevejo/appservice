package br.ucb.prevejo.transporte.terminal;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TerminalService {

    private final TerminalRepository repository;

    public TerminalService(TerminalRepository repository) {
        this.repository = repository;
    }

    public Optional<Terminal> obterByCodigo(String codigo) {
        return repository.findByCod(codigo);
    }

}
