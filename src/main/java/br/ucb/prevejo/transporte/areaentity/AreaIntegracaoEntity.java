package br.ucb.prevejo.transporte.areaentity;

import br.ucb.prevejo.transporte.areaintegracao.AreaIntegracao;
import br.ucb.prevejo.transporte.parada.ParadaDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class AreaIntegracaoEntity implements AreaEntity {

    @EqualsAndHashCode.Include
    private final AreaIntegracao areaIntegracao;

    @Override
    public Collection<ParadaDTO> paradas() {
        return areaIntegracao.getParadas().stream().map(p -> p.toDto()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return areaIntegracao.toString();
    }
}