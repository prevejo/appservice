package br.ucb.prevejo.transporte.trajeto;

import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.transporte.areaentity.ExpandableAreaEntity;
import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import br.ucb.prevejo.transporte.grafo.GrafoTransporte;
import br.ucb.prevejo.transporte.grafo.GrafoTransporteService;
import br.ucb.prevejo.transporte.grafo.dto.PercursoLigacao;
import br.ucb.prevejo.transporte.parada.ParadaFetch;
import br.ucb.prevejo.transporte.grafo.dto.AreaVertice;
import br.ucb.prevejo.transporte.grafo.dto.ArestaLigacao;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrajetoService {

    private final ParadaFetch paradaFetch;
    private final GrafoTransporteService grafoService;

    public TrajetoService(ParadaFetch paradaFetch, GrafoTransporteService grafoService) {
        this.paradaFetch = paradaFetch;
        this.grafoService = grafoService;
    }

    public Collection<Trajeto> search(AreaEntity origem, AreaEntity destino) {
        final Collection<Trajeto> trajetos = new ArrayList<>();
        trajetos.addAll(searchTrajetos(origem, destino));

        if (trajetos.isEmpty()) {
            Collection<ExpandableAreaEntity> expandableOnes = Arrays.asList(origem, destino).stream()
                    .filter(a -> a instanceof ExpandableAreaEntity)
                    .map(a -> (ExpandableAreaEntity) a)
                    .collect(Collectors.toList());

            Collections.runUtilAllComplete(
                    expandableOnes,
                    area -> area.expand(paradaFetch),
                    remainOnes -> !trajetos.addAll(searchTrajetos(origem, destino))
            );
        }

        return trajetos.stream().sorted().limit(10).collect(Collectors.toList());
    }

    private Collection<Trajeto> searchTrajetos(AreaEntity origem, AreaEntity destino) {
        GrafoTransporte grafo = grafoService.instance();

        AreaVertice origemVertice = grafo.addArea(origem);
        AreaVertice destinoVertice = grafo.addArea(destino);

        return grafo.searchPaths(origemVertice, destinoVertice).stream()
                .map(this::buildTrajeto)
                .collect(Collectors.toList());
    }

    private Trajeto buildTrajeto(List<ArestaLigacao> trechosLigacoes) {
        if (trechosLigacoes.isEmpty()) {
            throw new RuntimeException("Trajeto sem trechos");
        }

        AreaEntity origem = trechosLigacoes.get(0).getVerticeOrigem().getArea().getEntity();
        AreaEntity destino = trechosLigacoes.get(trechosLigacoes.size() - 1).getVerticeDestino().getArea().getEntity();
        List<Trecho> trechos = trechosLigacoes.stream()
                .map(this::buildTrecho)
                .collect(Collectors.toList());

        return new Trajeto(origem, destino, trechos);
    }

    private Trecho buildTrecho(ArestaLigacao trecho) {
        return new Trecho(
                trecho.getVerticeOrigem().getArea().getEntity(),
                trecho.getVerticeDestino().getArea().getEntity(),
                trecho.getPercursos().stream().map(this::buildPercurso).collect(Collectors.toList())
        );
    }

    private TrechoPercurso buildPercurso(PercursoLigacao percurso) {
        return new TrechoPercurso(
                percurso.getOrigem().getParadas().stream().map(p -> p.getParada()).collect(Collectors.toList()),
                percurso.getDestino().getParadas().stream().map(p -> p.getParada()).collect(Collectors.toList()),
                percurso.getPercurso()
        );
    }

}
