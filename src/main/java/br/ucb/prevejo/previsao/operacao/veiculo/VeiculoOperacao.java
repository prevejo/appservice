package br.ucb.prevejo.previsao.operacao.veiculo;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;

@Getter
@Setter
public class VeiculoOperacao {

    private InstanteOperacao instante;
    private Feature trechoRestante;

    private VeiculoOperacao(InstanteOperacao instante, Feature trechoRestante) {
        this.instante = instante;
        this.trechoRestante = trechoRestante;
    }

    public static VeiculoOperacao build(InstanteOperacao instante, FeatureCollection fc) {
        return new VeiculoOperacao(
                instante,
                (fc != null ? fc.getFeatures() : Collections.<Feature>emptyList())
                    .stream().filter(f -> "middle".equals(f.getProperties().get("position")))
                    .findFirst().orElse(null)
        );
    }

}
