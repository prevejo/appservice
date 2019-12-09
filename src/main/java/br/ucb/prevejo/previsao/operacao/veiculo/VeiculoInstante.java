package br.ucb.prevejo.previsao.operacao.veiculo;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class VeiculoInstante implements LocatedEntity {

    @JsonValue
    private InstanteOperacao instante;

    @JsonIgnore
    private List<Instante> historico;

    @JsonIgnore
    @Override
    public Point getLocation() {
        return instante.getLocation();
    }

    @JsonIgnore
    @Override
    public List<Point> getRecordPath() {
        return historico.stream().map(i -> i.getLocalizacao()).collect(Collectors.toList());
    }

}
