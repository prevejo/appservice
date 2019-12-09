package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.estimativa.EstimativaChegada;
import br.ucb.prevejo.previsao.estimativa.EstimativaPercurso;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.Geo;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.stream.Collectors;

public class HistoricoOperacao {

    private static final int FILL_DISTANCE = 100;
    private static final int MAX_POINT_DISTANCE = 30;
    private static final int MAX_DURACAO_PERCURSO_MINUTES = 130;

    private Collection<TurnoVeiculo> turnos;

    private HistoricoOperacao(Collection<TurnoVeiculo> turnos) {
        this.turnos = turnos;
    }

    public EstimativaPercurso calcularEstimativa(Percurso percurso, LocatedEntity endPoint, Collection<? extends LocatedEntity> startPoints) {
        Collection<TurnoVeiculo> turnos = getTurnos();

        List<EstimativaChegada> chegadas = startPoints.stream()
                .map(locatedEntity -> calcularChegada(locatedEntity, endPoint, turnos, percurso))
                .filter(ec -> ec.isPresent())
                .map(ec -> ec.get())
                .filter(ec -> ec.getDuracao() <= MAX_DURACAO_PERCURSO_MINUTES)
                .sorted()
                .collect(Collectors.toList());

        return new EstimativaPercurso(percurso.toDTO(), endPoint, chegadas);
    }

    private Optional<EstimativaChegada> calcularChegada(LocatedEntity startPoint, LocatedEntity endPoint, Collection<TurnoVeiculo> turnos, Percurso percurso) {
        Collection<TrechoStat> stats = turnos.stream()
                .map(turno -> turno.findAverageDuracaoTrecho(startPoint, endPoint, FILL_DISTANCE, MAX_POINT_DISTANCE))
                .filter(stat -> stat.isPresent())
                .map(stat -> stat.get()).collect(Collectors.toList());

        OptionalDouble duracao = stats.stream().mapToDouble(TrechoStat::getDuracao).average();
        OptionalDouble distancia = stats.stream().mapToDouble(TrechoStat::getDistancia).average();

        if (distancia.isPresent()) {    // Outlier remove
            double media = distancia.getAsDouble();
            stats = stats.stream()
                    .filter(s -> !(s.getDistancia() > media * 2))
                    .collect(Collectors.toList());

            duracao = stats.stream().mapToDouble(TrechoStat::getDuracao).average();
            distancia = stats.stream().mapToDouble(TrechoStat::getDistancia).average();
        }

        if (duracao.isPresent() && distancia.isPresent()) {
            Optional<FeatureCollection> trecho = splitPercurso(startPoint, endPoint, percurso, MAX_POINT_DISTANCE);
            return Optional.of(new EstimativaChegada(startPoint, duracao.getAsDouble(), distancia.getAsDouble(), trecho.orElse(null)));
        }

        return Optional.empty();
    }

    public Collection<TurnoVeiculo> getTurnos() {
        return turnos;
    }

    public static HistoricoOperacao build(Collection<InstanteOperacao> instantes) {
        return new HistoricoOperacao(instantes.stream()
                .collect(Collectors.groupingBy(op -> op.getVeiculo()))
                .entrySet().stream()
                .map(entryVeiculo -> new TurnoVeiculo(
                        entryVeiculo.getKey(),
                        entryVeiculo.getValue().stream()
                                .map(InstanteOperacao::getInstante)
                                .collect(br.ucb.prevejo.shared.util.Collections.sortedSetCollector())
                )).collect(Collectors.toList()));
    }

    private Optional<FeatureCollection> splitPercurso(LocatedEntity startPoint, LocatedEntity endPoint, Percurso percurso, int maxDistance) {
        LineString percursoLineStr = percurso.getGeo();
        List<Coordinate> coords = Geo.fillSequence(Arrays.asList(percursoLineStr.getCoordinates())
                                    .stream().map(c -> Geo.makePoint(c))
                                    .collect(Collectors.toList()), 50)
                                    .stream().map(p -> p.getCoordinate())
                                    .collect(Collectors.toList());

        List<Integer> listOfNears = Geo.findNearOnes(coords, startPoint.getLocation(), maxDistance)
                .stream().filter(index -> index < coords.size() - 1)
                .collect(Collectors.toList());

        if (startPoint.getRecordPath().size() >= 2) {
            List<Point> pointsFromStart = Geo.fillSequence(startPoint.getRecordPath(), 10);
            LineString lineStrFromStart = Geo.toLineString(pointsFromStart);
            double pathDistance = Geo.distance(startPoint.getRecordPath().iterator()).get();

            listOfNears = listOfNears.stream()
                    .filter(pathStart -> {
                        Iterator<Coordinate> beforePath = Collections.reserveIterator(coords.subList(0, pathStart + 1));
                        List<Coordinate> path = Geo.maxDistanceCoords(beforePath, (int) pathDistance);

                        if (path.size() < 2) {
                            return false;
                        }

                        LineString lineStrPath = Geo.toLineStringCoords(path);

                        double distanceFromStart = DiscreteHausdorffDistance.distance(lineStrPath, lineStrFromStart);

                        return distanceFromStart * 1000 < 3;
                    }).collect(Collectors.toList());
        }

        return listOfNears.stream()
                .map(pathStart -> Pair.of(
                        pathStart,
                        Geo.findNearOnes(coords.subList(pathStart, coords.size()), endPoint.getLocation(), maxDistance)
                                .stream().filter(i -> i > 0).collect(Collectors.toList())
                )).filter(pair -> !pair.getValue().isEmpty())
                .map(pair -> coords.subList(pair.getKey(), pair.getValue().stream().mapToInt(i -> i + pair.getKey()).min().getAsInt() + 1))
                .map(path -> Pair.of(path, Geo.distanceCoords(path.iterator())))
                .filter(pair -> pair.getValue().isPresent())
                .min(Comparator.comparingDouble(p -> p.getValue().get()))
                .map(pair -> Geo.splitLineString(coords, pair.getKey()))
                .map(fc -> FeatureCollection.build(fc.getFeatures().stream()
                        .filter(f -> !"end".equals(f.getProperties().get("position")))
                        .toArray(Feature[]::new)
                ));
    }

}
