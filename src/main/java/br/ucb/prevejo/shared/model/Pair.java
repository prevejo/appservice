package br.ucb.prevejo.shared.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Pair<K, V> {

    @EqualsAndHashCode.Include
    private K key;
    @EqualsAndHashCode.Include
    private V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair(key, value);
    }

}
