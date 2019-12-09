package br.ucb.prevejo.transporte.grafo.dto;

import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AreaParada {

    @EqualsAndHashCode.Include
    private ParadaDTO parada;
    private Collection<PercursoInstante> percursos;

    public Optional<PercursoInstante> getInstante(PercursoDTO percurso) {
        return getPercursos().stream().filter(instante -> instante.getPercurso().equals(percurso)).findFirst();
    }

    public boolean hasAnyInstanteAnterior(AreaParada other, PercursoDTO percurso) {
        return getInstante(percurso)
                .map(instante -> other.getInstante(percurso)
                        .map(instanteOther -> instante.hasInstanteAnterior(instanteOther))
                        .orElse(false))
                .orElse(false);
    }

}
