package br.ucb.prevejo.previsao.operacao.veiculo;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;

import java.time.Duration;
import java.util.*;

public class VeiculoHistorico {
    private static final Duration MAX_PAST_TIME = Duration.ofHours(3);
    private static final int MAX_DISTANCE = 1500;

    private InstanteOperacao current;
    private SortedSet<Instante> instantes = new TreeSet<>();

    public VeiculoHistorico(InstanteOperacao current) {
        push(current);
    }

    public boolean push(InstanteOperacao current) {
        if (this.current != null) {
            if (current.getInstante().getData().compareTo(this.current.getInstante().getData()) <= 0) {
                return false;
            }

            Instante first = this.instantes.first();

            if (!DateAndTime.isBehind(first.getData(), current.getInstante().getData(), MAX_PAST_TIME)) {
                instantes.remove(first);
            }

            if (Geo.distance(Collections.mapIterator(instantes.iterator(), Instante::getLocalizacao))
                    .map(distance -> distance + Geo.distanceBetween(this.current.getLocation(), current.getLocation()))
                    .flatMap(distance -> distance <= MAX_DISTANCE ? Optional.empty() : Optional.of(distance)).isPresent()) {
                instantes.remove(first);
            }
        }

        this.current = current;
        instantes.add(current.getInstante());

        return true;
    }

    public InstanteOperacao getInstanteOperacaoCurrent() {
        return current;
    }

    public Iterator<Instante> getInstantes() {
        return Collections.unmodifiableIterator(instantes.iterator());
    }

    public LocatedEntity toLocatedEntity() {
        return new VeiculoInstante(getInstanteOperacaoCurrent(), Collections.toList(getInstantes()));
    }

}
