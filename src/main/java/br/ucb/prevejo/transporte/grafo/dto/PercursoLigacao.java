package br.ucb.prevejo.transporte.grafo.dto;

import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PercursoLigacao {

    private ParadaLigacao origem;
    private ParadaLigacao destino;
    private PercursoDTO percurso;

}

