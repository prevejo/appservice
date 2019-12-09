package br.ucb.prevejo.transporte.grafo.dto;

import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class PercursoInstante {

    private PercursoDTO percurso;
    private Set<Integer> instantes;

    public boolean hasInstanteAnterior(PercursoInstante other) {
        return getInstantes().stream()
                .anyMatch(instante -> other.getInstantes().stream()
                        .anyMatch(instanteOther -> instante < instanteOther));
    }

}
