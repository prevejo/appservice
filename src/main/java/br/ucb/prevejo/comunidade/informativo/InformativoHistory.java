package br.ucb.prevejo.comunidade.informativo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class InformativoHistory {
    private static final int MAX_HISTORY_SIZE = 3;

    private LinkedList<Informativo> informativos = new LinkedList<>();

    public InformativoHistory(Collection<Informativo> informativos) {
        informativos.stream()
                .sorted(Comparator.comparing(Informativo::getDtPublicacao))
                .forEach(i -> push(i));
    }

    public Collection<Informativo> retriveAll() {
        return informativos.stream().collect(Collectors.toList());
    }

    public Collection<Informativo> retrive(LocalDateTime lastDateFrom) {
        return informativos.stream().filter(i -> i.getDtPublicacao().isAfter(lastDateFrom)).collect(Collectors.toList());
    }

    public void push(Informativo informativo) {
        if (informativos.size() >= MAX_HISTORY_SIZE) {
            informativos.pollFirst();
        }

        informativos.addLast(informativo);
    }

    public Optional<Informativo> last() {
        return Optional.ofNullable(informativos.peekLast());
    }

}
