package br.ucb.prevejo.previsao.operacao.register;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.shared.util.Geo;

import java.util.*;
import java.util.function.Consumer;

public class VeiculoQueue {
    private static final int SIGNIFICATIVE_DISTANCE = 5;
    private static final int MAX_QUEUE_SIZE = 1000;

    private Veiculo veiculo;
    private LinkedList<InstanteOperacao> queue = new LinkedList<>();

    public VeiculoQueue(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Collection<InstanteOperacao> pushAndPull(InstanteOperacao instante) {
        InstanteOperacao recent = queue.peekLast();

        if (queue.size() != 0) {
            if (instante.getInstante().compareTo(recent.getInstante()) > 0) {
                if (Geo.distanceBetween(instante.getLocation(), recent.getLocation()) >= SIGNIFICATIVE_DISTANCE) {
                    resetQueue(instante);
                    return queue.size() == 1 ? Arrays.asList(instante) : Arrays.asList(recent, instante);
                } else {
                    if (queue.size() == 1) {
                        queue.addLast(instante);
                    } else {
                        if (Geo.distanceBetween(instante.getLocation(), queue.peekFirst().getLocation()) >= SIGNIFICATIVE_DISTANCE) {
                            resetQueue(instante);
                            return Arrays.asList(instante);
                        } else {
                            if (queue.size() > MAX_QUEUE_SIZE) {
                                resetQueue(recent);
                            }
                            queue.addLast(instante);
                        }
                    }
                }
            }
        } else {
            queue.addLast(instante);
            return Arrays.asList(instante);
        }

        return Collections.emptyList();
    }

    private void resetQueue(InstanteOperacao newMostRecent) {
        queue.clear();
        queue.addLast(newMostRecent);
    }

}
