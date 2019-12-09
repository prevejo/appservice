package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.transporte.linha.Linha;

public interface PercursoDTO {

    Integer getId();
    EnumSentido getSentido();
    Linha getLinha();
    String getOrigem();
    String getDestino();

}
