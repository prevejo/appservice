package br.ucb.prevejo.shared.intefaces;

import br.ucb.prevejo.shared.model.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public interface PairIterator<T> extends Iterator<T> {

    public void forEachRemainingPair(BiConsumer<T, T> consumer);
    public List<Pair<T, T>> toSequencePairList();

}
