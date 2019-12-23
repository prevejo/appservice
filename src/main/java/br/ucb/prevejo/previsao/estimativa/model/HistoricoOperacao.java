package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.estimativa.EstimativaChegada;
import br.ucb.prevejo.previsao.estimativa.EstimativaPercurso;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.model.LineStringSpliter;
import br.ucb.prevejo.transporte.percurso.Percurso;

import java.util.*;
import java.util.stream.Collectors;

public class HistoricoOperacao {

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
                .map(turno -> turno.findAverageDuracaoTrecho(startPoint, endPoint, MAX_POINT_DISTANCE))
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
        LineStringSpliter spliter = new LineStringSpliter(percurso.getGeo());

        return spliter.split(startPoint, endPoint, maxDistance);
    }

}
