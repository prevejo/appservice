package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaDTO;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParadaCollectionEntity implements AreaEntity {

    @EqualsAndHashCode.Include
    private final Set<ParadaDTO> paradas;

    public ParadaCollectionEntity(Collection<Parada> paradas) {
        this.paradas = new HashSet<>(paradas.stream().map(Parada::toDto).collect(Collectors.toList()));
    }

    @Override
    public Collection<ParadaDTO> paradas() {
        return this.paradas;
    }

    @Override
    public String toString() {
        return "ParadaCollection(paradas=[" + paradas.stream().map(p -> p.getCod()).collect(Collectors.joining(",")) + "])";
    }

    protected void addParadas(Collection<Parada> paradas) {
        this.paradas.addAll(paradas.stream().map(Parada::toDto).collect(Collectors.toList()));
    }

}
