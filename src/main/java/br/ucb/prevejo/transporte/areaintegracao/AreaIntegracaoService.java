package br.ucb.prevejo.transporte.areaintegracao;

import br.ucb.prevejo.shared.model.FeatureCollection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

@Service
public class AreaIntegracaoService {

    private final AreaIntegracaoRepository repository;

    public AreaIntegracaoService(AreaIntegracaoRepository repository) {
        this.repository = repository;
    }

    public Collection<AreaIntegracao> obterAreas() {
        return repository.findAll();
    }

    public Collection<AreaIntegracaoDTO> obterAreasById(Collection<Integer> ids) {
        return repository.findByIdIn(ids);
    }

    public FeatureCollection obterCollectionById(Collection<Integer> ids) {
        return FeatureCollection.<AreaIntegracaoDTO>build(
                obterAreasById(ids),
                p -> p.getGeo(),
                p -> new HashMap<String, Object>() {{
                    put("id", p.getId());
                    put("descricao", p.getDescricao());
                }}
        );
    }

    public Collection<AreaIntegracao> obterAreasFetchParadas() {
        Specification<AreaIntegracao> spec = (Root<AreaIntegracao> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            root.fetch("paradas", JoinType.LEFT);
            return builder.isTrue(builder.literal(true));
        };

        return new LinkedHashSet(repository.findAll(spec));
    }

}
