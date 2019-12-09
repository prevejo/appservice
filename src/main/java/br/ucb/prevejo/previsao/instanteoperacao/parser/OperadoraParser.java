package br.ucb.prevejo.previsao.instanteoperacao.parser;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.operacao.EnumOperadora;
import br.ucb.prevejo.shared.intefaces.Parser;

import java.util.Collection;

public interface OperadoraParser extends Parser<String, Collection<InstanteOperacao>> {

    public EnumOperadora getOperadora();

}
