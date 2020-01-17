package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.core.cache.PassiveCacheService;
import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.transporte.parada.Parada;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.JoinType;
import java.util.*;

@Service
public class PercursoService {

    private final PercursoRepository repository;
    private final PassiveCacheService<?, Collection<PercursoDTO>> cacheService;

    public PercursoService(PercursoRepository repository, PassiveCacheService<?, Collection<PercursoDTO>> cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    public Optional<Percurso> obterPercurso(int id) {
        return repository.findById(id);
    }

    public Optional<Percurso> obterPercurso(String numLinha, EnumSentido sentido) {
        return repository.findPercursoByLinhaAndSentido(numLinha, sentido);
    }

    public Optional<PercursoDTO> obterPercursoDTO(String numLinha, EnumSentido sentido) {
        return repository.findPercursoDTOByLinhaAndSentido(numLinha, sentido);
    }

    public Optional<PercursoDTO> obterPercursoDTO(int id) {
        return repository.findPercursoDTOById(id);
    }

    public Optional<Percurso> obterPercursoFetchLinha(int id) {
        Specification spec = (root, query, builder) -> {
            root.fetch("linha", JoinType.LEFT);
            return builder.equal(root.get("id"), id);
        };

        return repository.findOne(spec);
    }

    public Optional<Percurso> obterPercursoFetchLinha(String numLinha, EnumSentido sentido) {
        Specification spec = (root, query, builder) -> {
            root.fetch("linha", JoinType.LEFT);
            return builder.and(
                    builder.equal(root.get("linha").get("numero"), numLinha),
                    builder.equal(root.get("sentido"), sentido)
            );
        };

        return repository.findOne(spec);
    }

    public Collection<Percurso> obterPercursosFetchParadas() {
        Specification spec = (root, query, builder) -> {
            root.fetch("paradas", JoinType.LEFT);
            return builder.isTrue(builder.literal(true));
        };

        return new LinkedHashSet(repository.findAll(spec));
    }

    Collection<Percurso> obterLinhaPercursosByPercursoId(int percursoId) {
        return repository.findPercursosByPercursoId(percursoId);
    }

    public Collection<PercursoGeoDTO> obterPercursos(Collection<Integer> ids) {
        return repository.findByIdIn(ids);
    }

    public Collection<PercursoDTO> obterPercursosByDescricao(String descricao) {
        return repository.findAllByDescricao(descricao);
    }

    public Collection<PercursoDTO> obterPercursos(Parada parada) {
        return repository.findAllByParada(parada);
    }

    public Collection<PercursoDTO> obterPercursosDTO() {
        return repository.findAllDTO();
    }

    public Collection<PercursoDTO> obterPercursosDTOFromCache() {
        return cacheService.getContentByClass(PercursoDTOCacheContent.class)
                .orElseThrow(() -> new RuntimeException("Cache não inicializado"));
    }

    public Feature obterFeature(Integer id) {
        return obterPercursos(Arrays.asList(id)).stream()
                .findFirst()
                .map(dto -> Feature.build(dto.getGeo(), new HashMap<String, Object>() {{
                    put("id", dto.getId());
                    put("origem", dto.getOrigem());
                    put("destino", dto.getDestino());
                    put("sentido", dto.getSentido());
                }})).orElse(null);
    }

    public FeatureCollection obterLinhaCollectionByPercursoId(int percursoId) {
        return FeatureCollection.build(
                obterLinhaPercursosByPercursoId(percursoId),
                p -> p.getGeo(),
                p -> new HashMap<String, Object>() {{
                    put("id", p.getId());
                    put("origem", p.getOrigem());
                    put("destino", p.getDestino());
                    put("sentido", p.getSentido());
                    put("linha", p.getLinha());
                }}
        );
    }

}
