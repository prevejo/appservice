package br.ucb.prevejo.transporte.grafo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ParadaLigacao {

    private Area area;
    private Collection<AreaParada> paradas;

}

