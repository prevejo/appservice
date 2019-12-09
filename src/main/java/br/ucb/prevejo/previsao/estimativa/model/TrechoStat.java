package br.ucb.prevejo.previsao.estimativa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrechoStat {

    private Double duracao;
    private Double distancia;

    public TrechoStat average(TrechoStat other) {
        return new TrechoStat(duracao + other.duracao / 2, distancia + other.distancia / 2);
    }

}
