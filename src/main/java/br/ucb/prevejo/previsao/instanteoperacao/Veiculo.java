package br.ucb.prevejo.previsao.instanteoperacao;

import br.ucb.prevejo.previsao.operacao.EnumOperadora;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Veiculo {

    @EqualsAndHashCode.Include
    private String numero;
    private EnumOperadora operadora;

}
