package br.ucb.prevejo.transporte.grafo;

import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import br.ucb.prevejo.transporte.areaentity.AreaIntegracaoEntity;
import br.ucb.prevejo.transporte.areaentity.ParadaCollectionEntity;
import br.ucb.prevejo.transporte.areaintegracao.AreaIntegracao;
import br.ucb.prevejo.transporte.grafo.dto.*;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.grafo.model.GrafoImpl;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import br.ucb.prevejo.transporte.percursoparada.PercursoParadaDTO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrafoTransporteImpl extends GrafoImpl<ArestaLigacao> implements GrafoTransporte {

    private final Map<Pair<PercursoDTO, ParadaDTO>, Set<Integer>> percursosParadaMap;
    private final Map<Area, AreaVertice> verticesMap = new HashMap<>();
    private final Set<AreaLigacao> ligacoes = new HashSet<>();

    public GrafoTransporteImpl(Collection<PercursoParadaDTO> percursosParadas) {
        this(Collections.emptyList(), percursosParadas);
    }

    public GrafoTransporteImpl(Collection<AreaLigacao> ligacoes, Collection<PercursoParadaDTO> percursosParadas) {
        this(ligacoes, percursosParadas.stream()
                .collect(Collectors.groupingBy(
                        pp -> Pair.of(pp.getPercurso(), pp.getParada()),
                        Collectors.mapping(pp -> pp.getSequencial(), Collectors.toSet())
                )));
    }

    private GrafoTransporteImpl(Collection<AreaLigacao> ligacoes, Map<Pair<PercursoDTO, ParadaDTO>, Set<Integer>> percursosParadaMap) {
        this.percursosParadaMap = percursosParadaMap;
        setInitialGrafo(ligacoes);
    }

    private void setInitialGrafo(Collection<AreaLigacao> ligacoes) {
        Stream.concat(
                ligacoes.stream().map(l -> l.getAreaA()),
                ligacoes.stream().map(l -> l.getAreaB())
        ).distinct().collect(Collectors.toMap(
                area -> area,
                area -> new AreaVertice(area)
        )).forEach((k, v) -> this.verticesMap.put(k, v));
        buildApontamentos(ligacoes, this.verticesMap);
        this.verticesMap.values().forEach(this::addVertice);
    }

    @Override
    public GrafoTransporte instance() {
        return new GrafoTransporteImpl(ligacoes(), this.percursosParadaMap);
    }

    @Override
    public Collection<AreaLigacao> ligacoes() {
        return ligacoes;
    }

    @Override
    public AreaVertice addArea(Parada parada) {
        return addArea(Arrays.asList(parada));
    }

    @Override
    public AreaVertice addArea(Collection<Parada> paradas) {
        return addArea(toAreaEntity(paradas));
    }

    @Override
    public AreaVertice addArea(AreaIntegracao areaIntegracao) {
        return addArea(toAreaEntity(areaIntegracao));
    }

    @Override
    public AreaVertice addArea(AreaEntity area) {
        return addArea(buildArea(area));
    }

    private AreaVertice addArea(Area area) {
        Collection<Area> areas = verticesMap.keySet();
        List<Pair<Area, Area>> newPairs = areas.stream()
                .map(a -> Pair.of(area, a))
                .collect(Collectors.toList());
        Collection<AreaLigacao> ligacoes = findAllLigacoes(newPairs);

        AreaVertice vertice = new AreaVertice(area);
        verticesMap.put(area, vertice);
        buildApontamentos(ligacoes, verticesMap);
        addVertice(vertice);

        return vertice;
    }

    private void buildApontamentos(Collection<AreaLigacao> ligacoes, Map<Area, AreaVertice> verticesMap) {
        for (AreaLigacao ligacao : ligacoes) {
            AreaVertice verticeA = verticesMap.get(ligacao.getAreaA());
            AreaVertice verticeB = verticesMap.get(ligacao.getAreaB());

            if (verticeA != null && verticeB != null) {
                if (!ligacao.getFromAtoB().isEmpty()) {
                    verticeA.apontar(verticeB, new ArestaLigacao(verticeA, verticeB, ligacao.getFromAtoB()));
                }
                if (!ligacao.getFromBtoA().isEmpty()) {
                    verticeB.apontar(verticeA, new ArestaLigacao(verticeB, verticeA, ligacao.getFromBtoA()));
                }

                this.ligacoes.add(ligacao);
            }
        }
    }

    private Area buildArea(AreaEntity areaEntity) {
        Collection<AreaParada> paradas = areaEntity.paradas().stream()
                .map(parada -> new AreaParada(
                        parada,
                        percursosParadaMap.entrySet().stream()
                                .filter(e -> e.getKey().getValue().equals(parada))
                                .map(e -> new PercursoInstante(e.getKey().getKey(), e.getValue()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new Area(areaEntity, paradas);
    }

    private Collection<AreaLigacao> findAllLigacoes(Collection<Pair<Area, Area>> areaPairs) {
        return areaPairs.stream()
                .map(pair -> buildLigacao(pair.getKey(), pair.getValue()))
                .filter(ligacao -> !ligacao.getFromAtoB().isEmpty() || !ligacao.getFromBtoA().isEmpty())
                .collect(Collectors.toList());
    }

    private AreaLigacao buildLigacao(Area areaA, Area areaB) {
        return new AreaLigacao(
                areaA,
                areaB,
                findLigacoesOrigemDestino(areaA, areaB),
                findLigacoesOrigemDestino(areaB, areaA)
        );
    }

    private Collection<PercursoLigacao> findLigacoesOrigemDestino(Area areaOrigem, Area areaDestino) {
        Map<PercursoDTO, Pair<List<AreaParada>, List<AreaParada>>> intercisos = findPercursosIntercisos(areaOrigem.getParadas(), areaDestino.getParadas());

        return intercisos.entrySet().stream()
                .map(e -> {
                    List<Pair<AreaParada, AreaParada>> fromOrigemToDestino = e.getValue().getKey().stream()
                            .flatMap(paradaOrigem -> e.getValue().getValue().stream()
                                    .filter(paradaDestino -> paradaOrigem.hasAnyInstanteAnterior(paradaDestino, e.getKey()))
                                    .map(paradaDestino -> Pair.of(paradaOrigem, paradaDestino)))
                            .collect(Collectors.toList());

                    return Pair.of(e.getKey(), fromOrigemToDestino);
                }).filter(pair -> !pair.getValue().isEmpty())
                .map(pair -> new PercursoLigacao(
                        new ParadaLigacao(areaOrigem, pair.getValue().stream().map(p -> p.getKey()).collect(Collectors.toSet())),
                        new ParadaLigacao(areaDestino, pair.getValue().stream().map(p -> p.getValue()).collect(Collectors.toSet())),
                        pair.getKey()
                )).collect(Collectors.toList());
    }

    private Map<PercursoDTO, Pair<List<AreaParada>, List<AreaParada>>> findPercursosIntercisos(Collection<AreaParada> paradasOrigem, Collection<AreaParada> paradasDestino) {
        Map<PercursoDTO, List<AreaParada>> areaPercursos = groupByPercurso(paradasOrigem);
        Map<PercursoDTO, List<AreaParada>> otherPercursos = groupByPercurso(paradasDestino);

        return areaPercursos.entrySet().stream()
                .filter(e -> otherPercursos.containsKey(e.getKey()))
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> Pair.of(e.getValue(), otherPercursos.get(e.getKey()))
                ));
    }

    private Map<PercursoDTO, List<AreaParada>> groupByPercurso(Collection<AreaParada> paradas) {
        return paradas.stream()
                .flatMap(parada -> parada.getPercursos()
                        .stream()
                        .map(pi -> pi.getPercurso())
                        .map(percurso -> Pair.of(percurso, parada))
                ).collect(Collectors.groupingBy(
                        p -> p.getKey(),
                        Collectors.mapping(p -> p.getValue(), Collectors.toList())
                ));
    }

    private AreaEntity toAreaEntity(AreaIntegracao areaIntegracao) {
        return new AreaIntegracaoEntity(areaIntegracao);
    }

    private AreaEntity toAreaEntity(Collection<Parada> paradas) {
        return new ParadaCollectionEntity(paradas);
    }

}
