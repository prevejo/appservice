package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.shared.intefaces.PairIterator;
import br.ucb.prevejo.shared.model.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Collections {

    public static <T> Collection<List<T>> split(List<T> list, int splitLength) {
        int current = 0;
        List<List<T>> splits = new ArrayList<>();

        while (list.size() - current >= splitLength) {
            splits.add(list.subList(current, current + splitLength));

            current += splitLength;
        }

        if (list.size() - current > 0) {
            splits.add(list.subList(current, list.size()));
        }

        return splits;
    }

    public static <T> Iterator<T> unmodifiableIterator(Iterator<T> iterator) {
        return new UnmodifiableIterator<>(iterator);
    }

    public static <T> Iterator<T> reserveIterator(SortedSet<T> sortedSet) {
        return new ReverseIterator<>(sortedSet);
    }

    public static <T> Iterator<T> reserveIterator(List<T> list) {
        return new ListReverseIterator<>(list);
    }

    public static <T> List<T> toList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }

    public static <T> SortedSet<T> newSubset(T first, T last, SortedSet<T> set) {
        SortedSet<T> newSet = new TreeSet<>(set.subSet(first, last));

        newSet.add(last);

        return newSet;
    }

    public static <T> Optional<T> findElement(SortedSet<T> set, int index) {
        return findElement(set.iterator(), index);
    }

    public static <T> Optional<T> findElement(Iterator<T> it, int index) {
        int count = 0;

        T elementToReturn = null;
        while (it.hasNext()) {
            T element = it.next();

            if (count == index) {
                elementToReturn = element;
                break;
            }

            count++;
        }

        return Optional.ofNullable(elementToReturn);
    }

    public static <T, R> Iterator<R> mapIterator(Iterator<T> iterator, Function<T, R> mapFunction) {
        return new IteratorMap(iterator, mapFunction);
    }

    public static <T> PairIterator<T> pairIteratorOf(Iterator<T> it) {
        return new PairIteratorImpl(it);
    }

    public static <T> Collector<T, ?, SortedSet<T>> sortedSetCollector() {
        return Collector.of(
                TreeSet::new,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }
        );
    }

    public static <K, V> List<Pair<K, V>> joinInPairs(List<K> listA, List<V> listB) {
        Iterator<K> itA = listA.iterator();
        Iterator<V> itB = listB.iterator();
        List<Pair<K, V>> resultList = new ArrayList<>();

        while (itA.hasNext() && itB.hasNext()) {
            resultList.add(Pair.of(itA.next(), itB.next()));
        }

        return resultList;
    }

    public static <T> Collection<Pair<T, T>> buildUniquePairs(Collection<T> objects) {
        return objects.stream().collect(Collectors.toList()).stream()
                .flatMap(area -> objects.stream()
                        .filter(other -> other != area)
                        .map(other -> Pair.of(area, other))
                ).collect(Collectors.groupingBy(
                        pair -> new HashSet(Arrays.asList(pair.getKey(), pair.getValue()))
                )).values().stream()
                .map(lists -> lists.stream().findFirst().get())
                .collect(Collectors.toList());
    }

    public static <T> void runUtilAllComplete(Collection<T> objects, Function<T, Boolean> task, Function<Collection<T>, Boolean> afterAllRun) {
        List<T> list = new ArrayList<>(objects);

        while(!list.isEmpty()) {
            Iterator<T> it = list.iterator();

            while (it.hasNext()) {
                if (!task.apply(it.next())) {
                    it.remove();
                }
            }

            if (!list.isEmpty()) {
                if (!afterAllRun.apply(list)) {
                    break;
                }
            }
        }
    }

    private static class PairIteratorImpl<T> implements PairIterator<T> {
        private Iterator<T> it;

        public PairIteratorImpl(Iterator<T> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public T next() {
            return it.next();
        }

        public void forEachRemainingPair(BiConsumer<T, T> consumer) {
            T prev = null;

            while (hasNext()) {
                T next = next();

                if (prev != null) {
                    consumer.accept(prev, next);
                }

                prev = next;
            }
        }

        public List<Pair<T, T>> toSequencePairList() {
            List<Pair<T, T>> list = new ArrayList<>();

            forEachRemainingPair((elem1, elem2) -> list.add(Pair.of(elem1, elem2)));

            return list;
        }
    }

    private static class IteratorMap<T, R> implements Iterator<R> {
        private Iterator<T> iterator;
        private Function<T, R> mapFunction;

        public IteratorMap(Iterator<T> iterator, Function<T, R> mapFunction) {
            this.iterator = iterator;
            this.mapFunction = mapFunction;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public R next() {
            return mapFunction.apply(iterator.next());
        }
    }


    private static class ReverseIterator<T> implements Iterator<T> {
        private SortedSet<T> set;

        public ReverseIterator(SortedSet<T> set) {
            this.set = set;
        }

        @Override
        public boolean hasNext() {
            return !set.isEmpty();
        }

        @Override
        public T next() {
            T current = set.last();
            set = set.headSet(current);
            return current;
        }
    }

    private static class ListReverseIterator<T> implements Iterator<T> {
        private List<T> list;
        private int currentIndex;

        public ListReverseIterator(List<T> list) {
            this.list = list;
            this.currentIndex = list.size();
        }

        @Override
        public boolean hasNext() {
            return currentIndex - 1 >= 0;
        }

        @Override
        public T next() {
            if (currentIndex > 0) {
                currentIndex -= 1;

                return this.list.get(currentIndex);
            }

            throw new IllegalStateException("Invalid index");
        }
    }

    private static class UnmodifiableIterator<T> implements Iterator<T> {
        private Iterator<T> iterator;

        public UnmodifiableIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

}
