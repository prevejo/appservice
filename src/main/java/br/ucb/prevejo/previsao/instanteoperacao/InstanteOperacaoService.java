package br.ucb.prevejo.previsao.instanteoperacao;

import br.ucb.prevejo.core.ContextProvider;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstanteOperacaoService {

    private final InstanteOperacaoRepository repository;

    public InstanteOperacaoService(InstanteOperacaoRepository repository) {
        this.repository = repository;
    }

    public Collection<InstanteOperacao> obterByPercurso(PercursoDTO percurso) {
        List<Integer> diasSemana = Arrays.asList(DateAndTime.now().getDayOfWeek().ordinal() + 1);
        if (Arrays.asList(ContextProvider.getBean(Environment.class).getActiveProfiles()).stream().anyMatch(p -> "devmem".equals(p))) {
            diasSemana = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        }

        if (percurso.getSentido() == EnumSentido.CIRCULAR) {
            return repository.findAllByLinha(percurso.getLinha().getNumero(), diasSemana)
                .stream().map(inst -> {
                    inst.setSentido(EnumSentido.CIRCULAR);
                    return inst;
                }).collect(Collectors.toList());
        }

        return repository.findAllByLinhaAndSentido(percurso.getLinha().getNumero(), percurso.getSentido(), diasSemana);
    }

    public Collection<InstanteOperacao> obterByPercurso(PercursoDTO percurso, LocalTime startTime, LocalTime endTime) {
        if (percurso.getSentido() == EnumSentido.CIRCULAR) {
            return repository.findAllByLinhaInRange(percurso.getLinha().getNumero(), startTime, endTime)
                    .stream().map(inst -> {
                        inst.setSentido(EnumSentido.CIRCULAR);
                        return inst;
                    }).collect(Collectors.toList());
        }

        return repository.findAllByLinhaAndSentidoInRange(percurso.getLinha().getNumero(), percurso.getSentido(), startTime, endTime);
    }

    public void registarInstantes(Collection<InstanteOperacao> instantes) {
        repository.batchInsert(new ArrayList<>(instantes));
    }

}
