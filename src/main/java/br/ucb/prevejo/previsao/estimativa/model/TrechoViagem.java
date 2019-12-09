package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.SortedSet;

@Getter
public class TrechoViagem {

    private Instante inicio;
    private Instante fim;
    private SortedSet<Instante> trecho;

    private TrechoViagem(Instante inicio, Instante fim, SortedSet<Instante> trecho) {
        this.inicio = inicio;
        this.fim = fim;
        this.trecho = trecho;
    }

    public Duration calcularDuracao() {
        return Duration.of(DateAndTime.timeBetween(getInicio().getData(), getFim().getData(), ChronoUnit.MILLIS), ChronoUnit.MILLIS);
    }

    public Optional<Double> calcularDistancia() {
        return Geo.distance(Collections.mapIterator(getTrecho().iterator(), inst -> inst.getLocalizacao()));
    }

    public static TrechoViagem buildTrecho(SortedSet<Instante> trecho) {
        if (trecho.size() < 2) {
            throw new IllegalArgumentException("Invalid set size");
        }

        return new TrechoViagem(trecho.first(), trecho.last(), trecho);
    }

}
