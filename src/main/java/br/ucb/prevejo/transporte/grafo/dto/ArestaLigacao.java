package br.ucb.prevejo.transporte.grafo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ArestaLigacao {

    private AreaVertice verticeOrigem;
    private AreaVertice verticeDestino;
    private Collection<PercursoLigacao> percursos;
}
