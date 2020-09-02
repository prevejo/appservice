package br.ucb.prevejo.previsao.operacao.register;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.previsao.operacao.Operacao;
import br.ucb.prevejo.shared.util.StringUtil;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OperacaoRegister {

    private static final Duration MAX_PAST_TIME = Duration.of(5, ChronoUnit.MINUTES);
    private static final int BUFFER_SIZE = 1000;

    private final Supplier<Collection<PercursoDTO>> percursosSupplier;
    private Map<Veiculo, VeiculoQueue> queueMap = new HashMap<>();

    private List<InstanteOperacao> buffer = new LinkedList<>();
    private Consumer<Collection<InstanteOperacao>> instantesConsumer;
    private Consumer<Collection<InstanteOperacao>> instantesFlushConsumer;
    private Lock registerLock = new ReentrantLock(true);

    public OperacaoRegister(Supplier<Collection<PercursoDTO>> percursosSupplier, Consumer<Collection<InstanteOperacao>> instantesConsumer) {
        this.percursosSupplier = percursosSupplier;
        this.instantesConsumer = instantesConsumer;
    }

    public void setInstantesFlushConsumer(Consumer<Collection<InstanteOperacao>> instantesFlushConsumer) {
        this.instantesFlushConsumer = instantesFlushConsumer;
    }

    public void register(Collection<Operacao> operacoes) {
        registerLock.lock();

        try {
            List<InstanteOperacao> instantes = filterRecentes(filterEmOperacao(operacoes)).stream()
                    .flatMap(op -> op.getVeiculos().stream())
                    .flatMap(instante -> getQueue(instante.getVeiculo()).pushAndPull(instante).stream())
                    .collect(Collectors.toList());

            if (instantesFlushConsumer != null) {
                instantesFlushConsumer.accept(instantes);
            }
            registerInstantes(instantes);
        } finally {
            registerLock.unlock();
        }
    }

    private void registerInstantes(Collection<InstanteOperacao> instantes) {
        if (instantes.isEmpty()) {
            return;
        }

        List<InstanteOperacao> remainingInstantes = Collections.emptyList();

        int remainingCapacity = BUFFER_SIZE - buffer.size();

        if (instantes.size() > remainingCapacity) {
            remainingInstantes = new ArrayList<>(instantes);
            instantes = remainingInstantes.subList(0, remainingCapacity);
            remainingInstantes = remainingInstantes.subList(remainingCapacity, remainingInstantes.size());
        }

        buffer.addAll(instantes);

        if (buffer.size() == BUFFER_SIZE) {
            instantes = new ArrayList<>(buffer);
            buffer.clear();
            instantesConsumer.accept(instantes);
        }

        if (!remainingInstantes.isEmpty()) {
            registerInstantes(remainingInstantes);
        }
    }

    private Collection<Operacao> filterRecentes(Collection<Operacao> operacoes) {
        return operacoes.stream()
                .map(op -> op.filterRecentOnes(MAX_PAST_TIME))
                .collect(Collectors.toList());
    }

    private Collection<Operacao> filterEmOperacao(Collection<Operacao> operacoes) {
        Map<String, List<PercursoDTO>> byLinha = percursosSupplier.get().stream().collect(Collectors.groupingBy(p -> p.getLinha().getNumero()));

        return operacoes.stream()
                .map(op -> op.filter(inst -> {

                    if (!StringUtil.isEmpty(inst.getLinha()) && byLinha.containsKey(inst.getLinha())) {
                        List<PercursoDTO> percursos = byLinha.get(inst.getLinha());

                        if (percursos.size() == 1 && percursos.get(0).getSentido() == EnumSentido.CIRCULAR) {
                            inst.setSentido(EnumSentido.CIRCULAR);
                            return true;
                        }

                        return percursos.stream().anyMatch(perc -> perc.getSentido() == inst.getSentido());
                    }

                    return false;
                })).collect(Collectors.toList());
    }

    private VeiculoQueue getQueue(Veiculo veiculo) {
        return Optional.ofNullable(queueMap.get(veiculo))
                .orElseGet(() -> {
                    VeiculoQueue queue = new VeiculoQueue(veiculo);
                    queueMap.put(veiculo, queue);
                    return queue;
                });
    }

}
