package br.ucb.prevejo.transporte.grafo.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AreaLigacao {

    @EqualsAndHashCode.Include
    private Area areaA;
    @EqualsAndHashCode.Include
    private Area areaB;
    private Collection<PercursoLigacao> fromAtoB;
    private Collection<PercursoLigacao> fromBtoA;

}
