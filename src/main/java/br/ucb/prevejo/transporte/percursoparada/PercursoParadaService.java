package br.ucb.prevejo.transporte.percursoparada;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.JoinType;
import java.util.Collection;

@Service
public class PercursoParadaService {

    private final PercursoParadaRepository repository;

    public PercursoParadaService(PercursoParadaRepository repository) {
        this.repository = repository;
    }

    public Collection<PercursoParada> obterPercursosParadas() {
        Specification spec = (root, query, builder) -> {
            root.fetch("percurso", JoinType.LEFT);
            root.fetch("percurso", JoinType.LEFT).fetch("linha", JoinType.LEFT);
            root.fetch("parada", JoinType.LEFT);
            return builder.isTrue(builder.literal(true));
        };

        return repository.findAll(spec);
    }

    public Collection<PercursoParadaDTO> obterPercursosParadasDTO() {
        return repository.findDto();
    }

}
