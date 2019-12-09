package br.ucb.prevejo.previsao.estimativa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AtDistance<T> implements Comparable<AtDistance> {

    private T entity;

    @EqualsAndHashCode.Include
    private double distance;

    @Override
    public int compareTo(AtDistance other) {
        return Double.compare(getDistance(), other.getDistance());
    }

    public static <T> AtDistance<T> build(T entity, double distance) {
        return new AtDistance<>(entity, distance);
    }

}
