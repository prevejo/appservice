package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.Geo;
import lombok.Getter;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TurnoVeiculo {

    private Veiculo veiculo;
    private SortedSet<Instante> instantes;

    public TurnoVeiculo(Veiculo veiculo, SortedSet<Instante> instantes) {
        this.veiculo = veiculo;
        this.instantes = instantes;
    }

    public Optional<TrechoStat> findAverageDuracaoTrecho(LocatedEntity origin, LocatedEntity destination, int maxPointDistance) {
        List<TrechoViagem> trechos = findTrechos(origin, destination, getInstantes(), maxPointDistance);

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

    private List<TrechoViagem> findTrechos(LocatedEntity origin, LocatedEntity destination, SortedSet<Instante> instantesTurno, int maxDistance) {
        List<Instante> nearFromBus = findInstantesNear(instantesTurno, origin.getLocation(), maxDistance);
        List<Instante> nearFromEmbarque = findInstantesNear(instantesTurno, destination.getLocation(), maxDistance);

        SortedSet<Instante> instantesPreenchidos = new TreeSet<>(instantesTurno);
        instantesPreenchidos.addAll(nearFromBus);
        instantesPreenchidos.addAll(nearFromEmbarque);

        List<TrechoViagem> trechos = findTrechos(nearFromBus, nearFromEmbarque, instantesPreenchidos);

        List<Point> originPoints = origin.getRecordPath();

        if (originPoints.size() < 2) {
            return trechos;
        }

        return filterTrechosBySentido(trechos, originPoints, instantesPreenchidos);
    }

    private List<TrechoViagem> filterTrechosBySentido(List<TrechoViagem> trechos, List<Point> sentidoPoints, SortedSet<Instante> instantesTurno) {
        List<Point> pointsFromBus = sentidoPoints;
        LineString lineStrBus = Geo.toLineString(pointsFromBus);
        double distance = Geo.distance(sentidoPoints.iterator()).get();

        return trechos.stream().filter(trecho -> {
            List<Point> beforeTrecho = Collections.toList(Collections.mapIterator(Collections.reserveIterator(instantesTurno.headSet(trecho.getInicio())), Instante::getLocalizacao));
            List<Point> beforeTrechoRange = Geo.maxDistanceWithCut(beforeTrecho, (int)distance);

            if (beforeTrechoRange.size() > 1) {
                LineString lineStrTrecho = Geo.toLineString(beforeTrechoRange);

                double distanceFromBus = DiscreteHausdorffDistance.distance(lineStrTrecho, lineStrBus) * 1000;

                return distanceFromBus < 1;
            }

            return false;
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
        Iterator<InstanteSegment> segmentos = Collections.pairIteratorOf(instantes.iterator()).toSequencePairList()
                .stream().map(pair -> InstanteSegment.build(pair.getKey(), pair.getValue())).iterator();

        List<InstanteSegment> nearOnes = Geo.findSegmentsNear(segmentos, point, maxDistance);

        return nearOnes.stream().map(seg -> seg.middlePoint(point)).collect(Collectors.toList());
    }

}
