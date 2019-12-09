package br.ucb.prevejo.previsao.operacao;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Operacao {

	private EnumOperadora operadora;
	private Collection<InstanteOperacao> veiculos;
	
	public Operacao() {
	}

	public Operacao filterRecentOnes(Duration maxPastDuration) {
		return filter(v -> v.getInstante().isBehind(maxPastDuration));
	}

	public Operacao filter(Predicate<InstanteOperacao> predicateForInstante) {
		return new Operacao(
				operadora,
				veiculos.stream()
						.filter(predicateForInstante)
						.collect(Collectors.toList())
		);
	}

}
