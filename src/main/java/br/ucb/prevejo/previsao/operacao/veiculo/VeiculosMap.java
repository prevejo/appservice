package br.ucb.prevejo.previsao.operacao.veiculo;

import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VeiculosMap {

    private Map<Veiculo, VeiculoHistorico> map = new HashMap<>();

    public void updateMap(Collection<InstanteOperacao> instantes) {
        instantes.forEach(this::updateVeiculo);
    }

    private void updateVeiculo(InstanteOperacao instante) {
        synchronized (map) {
            VeiculoHistorico historico = map.get(instante.getVeiculo());

            if (historico == null) {
                map.put(instante.getVeiculo(), new VeiculoHistorico(instante));
            } else {
                historico.push(instante);
            }
        }
    }

    public Collection<VeiculoHistorico> getHistorico(PercursoDTO percurso) {
        synchronized (map) {
            return map.values().stream()
                    .filter(historico -> historico.getInstanteOperacaoCurrent().assignTo(percurso))
                    .collect(Collectors.toList());
        }
    }

    public Optional<VeiculoHistorico> getHistorico(Veiculo veiculo) {
        return Optional.ofNullable(map.get(veiculo));
    }

}
