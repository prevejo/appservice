package br.ucb.prevejo.previsao.estimativa;

import br.ucb.prevejo.core.TimeSerializer;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.util.DateAndTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@AllArgsConstructor
public class EstimativaChegada implements Comparable<EstimativaChegada> {

    private LocatedEntity startPoint;
    private Double duracao;
    private Double distancia;
    private FeatureCollection trecho;

    @JsonSerialize(using = TimeSerializer.class)
    public LocalDateTime getHoraPrevista() {
        return DateAndTime.now().plusMinutes(duracao.intValue());
    }

    @Override
    public int compareTo(EstimativaChegada other) {
        return Comparator.comparing(EstimativaChegada::getDuracao)
                .thenComparing(EstimativaChegada::getDistancia)
                .compare(this, other);
    }

}
