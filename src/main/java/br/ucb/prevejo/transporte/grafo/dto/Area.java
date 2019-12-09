package br.ucb.prevejo.transporte.grafo.dto;

import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Area {

    @EqualsAndHashCode.Include
    private AreaEntity entity;

    private Collection<AreaParada> paradas;

    @Override
    public String toString() {
        return entity.toString();
    }
}
