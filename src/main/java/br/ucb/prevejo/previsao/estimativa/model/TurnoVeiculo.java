package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;
import lombok.Getter;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class TurnoVeiculo {

    private Veiculo veiculo;
    private SortedSet<Instante> instantes;
    private SortedSet<Instante> instantesFilled;

    public TurnoVeiculo(Veiculo veiculo, SortedSet<Instante> instantes) {
        this.veiculo = veiculo;
        this.instantes = instantes;
    }


    public SortedSet<Instante> getInstantesFilled(int distance) {
        if (instantesFilled == null) {
            instantesFilled = fillInstanteSet(getInstantes(), distance);
            //return getInstantes();
        }

        return instantesFilled;
    }

    public Optional<TrechoStat> findAverageDuracaoTrecho(LocatedEntity origin, LocatedEntity destination, int filledDistance, int maxPointDistance) {
        List<TrechoViagem> trechos = findTrechos(origin, destination, filledDistance, maxPointDistance);

        OptionalDouble duracao = trechos.stream()
            .mapToLong(t -> t.calcularDuracao().toMinutes())
            .average();

        OptionalDouble distancia = trechos.stream()
            .map(t -> t.calcularDistancia())
            .filter(distance -> distance.isPresent())
            .mapToDouble(distance -> distance.get())
            .average();

        if (duracao.isPresent() && distancia.isPresent()) {
            return Optional.of(new TrechoStat(duracao.getAsDouble(), distancia.getAsDouble()));
        }

        return Optional.empty();
    }

    public List<TrechoViagem> findTrechos(LocatedEntity origin, LocatedEntity destination, int filledDistance, int maxPointDistance) {
        SortedSet<Instante> instantesFilled = getInstantesFilled(filledDistance);

        return findTrechos(origin, destination, instantesFilled, maxPointDistance);
    }

    private List<TrechoViagem> findTrechos(LocatedEntity origin, LocatedEntity destination, SortedSet<Instante> instantesTurno, int maxDistance) {
        List<Instante> nearFromBus = findInstantesNear(instantesTurno, origin.getLocation(), maxDistance);
        List<Instante> nearFromEmbarque = findInstantesNear(instantesTurno, destination.getLocation(), maxDistance);

        List<TrechoViagem> trechos = findTrechos(nearFromBus, nearFromEmbarque, instantesTurno);

        List<Point> originPoints = origin.getRecordPath();

        if (originPoints.size() < 2) {
            return trechos;
        }

        return filterTrechosBySentido(trechos, originPoints, instantesTurno);
    }

    private List<TrechoViagem> filterTrechosBySentido(List<TrechoViagem> trechos, List<Point> sentidoPoints, SortedSet<Instante> instantesTurno) {
        List<Point> pointsFromBus = Geo.fillSequence(sentidoPoints, 10);
        LineString lineStrBus = Geo.toLineString(pointsFromBus);
        double distance = Geo.distance(sentidoPoints.iterator()).get();

        return trechos.stream().filter(trecho -> {
            Iterator<Point> beforeTrecho = Collections.mapIterator(Collections.reserveIterator(instantesTurno.headSet(trecho.getInicio())), Instante::getLocalizacao);
            List<Point> beforeTrechoRange = Geo.fillSequence(Geo.maxDistance(beforeTrecho, (int)distance), 10);
            LineString lineStrTrecho = Geo.toLineString(beforeTrechoRange);

            double distanceFromBus = DiscreteHausdorffDistance.distance(lineStrTrecho, lineStrBus);

            return distanceFromBus * 1000 < 1;
        }).collect(Collectors.toList());
    }


    private List<TrechoViagem> findTrechos(List<Instante> nearFromOrigin, List<Instante> nearFromDestination, SortedSet<Instante> instantes) {
        return findTrechos(nearFromOrigin, nearFromDestination)
                .stream().map(trecho -> TrechoViagem.buildTrecho(
                        br.ucb.prevejo.shared.util.Collections.newSubset(trecho.getKey(), trecho.getValue(), instantes)
                )).collect(Collectors.toList());
    }

    private List<Pair<Instante, Instante>> findTrechos(List<Instante> nearFromOrigin, List<Instante> nearFromDestination) {
        List<Pair<Instante, Instante>> trechos = new ArrayList<>();

        Iterator<Instante> iteratorOrigin = nearFromOrigin.iterator();
        SortedSet<Instante> destinationSet = new TreeSet<>(nearFromDestination);

        while (iteratorOrigin.hasNext()) {
            Instante origin = iteratorOrigin.next();

            destinationSet.add(origin);

            SortedSet<Instante> fromOrigin = destinationSet.tailSet(origin);
            fromOrigin.remove(origin);

            try {
                Instante destination = fromOrigin.first();
                destinationSet.add(destination);

                trechos.add(Pair.of(origin, destination));
            } catch(NoSuchElementException e) {}
        }

        return trechos;
    }


    private List<Instante> findInstantesNear(SortedSet<Instante> instantes, Point point, int maxDistance) {
        Iterator<Instante> it = instantes.iterator();

        List<Instante> listOfNears = new ArrayList<>();
        SortedSet<AtDistance<Instante>> nearBag = new TreeSet<>();
        boolean entryFlag = false;

        while (it.hasNext()) {
            Instante instante = it.next();
            double distance = Geo.distanceBetween(instante.getLocalizacao(), point);

            if (distance <= maxDistance) {
                entryFlag = true;

                nearBag.add(AtDistance.build(instante, distance));
            } else {
                if (entryFlag) {
                    entryFlag = false;

                    listOfNears.add(nearBag.first().getEntity());
                    nearBag.clear();
                }
            }
        }

        return listOfNears;
    }

    private SortedSet<Instante> fillInstanteSet(SortedSet<Instante> instantes, int distance) {
        List<Pair<Instante, Instante>> list = br.ucb.prevejo.shared.util.Collections.pairIteratorOf(instantes.iterator()).toSequencePairList();

        Stream<Instante> firstPoint = list.stream()
                .findFirst()
                .map(pair -> Stream.of(pair.getKey()))
                .orElse(Stream.empty());

        Stream<Instante> otherPoints = list.stream()
                .flatMap(pair -> Stream.concat(
                        middleInstantes(pair.getKey(), pair.getValue(), distance),
                        Stream.of(pair.getValue())
                ));

        List<Instante> filled = Stream.concat(firstPoint, otherPoints).collect(Collectors.toList());

        return new TreeSet<>(filled);
    }

    private Stream<Instante> middleInstantes(Instante first, Instante second, int distance) {
        List<Point> points = Geo.makePointsBetween(first.getLocalizacao(), second.getLocalizacao(), distance);

        List<LocalDateTime> interval = DateAndTime.splitInterval(first.getData(), second.getData(), points.size() - 2);

        List<Pair<Point, LocalDateTime>> pairsList = br.ucb.prevejo.shared.util.Collections.joinInPairs(points, interval);

        Stream<Instante> middleOnes = pairsList.size() > 2
                ? pairsList.subList(1, pairsList.size() - 1).stream().map(pair -> new Instante(pair.getValue(), pair.getKey()))
                : Stream.empty();

        return Stream.concat(
                Stream.concat(Stream.of(first), middleOnes),
                Stream.of(second)
        );
    }

}
