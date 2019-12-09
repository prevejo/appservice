package br.ucb.prevejo.previsao.instanteoperacao;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
public class InstanteOperacao implements LocatedEntity {

    private Veiculo veiculo;
    private String linha;
    private EnumSentido sentido;
    private Instante instante;

    public void setSentido(EnumSentido sentido) {
        this.sentido = sentido;
    }

    @JsonIgnore
    @Override
    public Point getLocation() {
        return getInstante().getLocalizacao();
    }

    public boolean assignTo(PercursoDTO percurso) {
        return percurso.getLinha().getNumero().equals(getLinha()) && percurso.getSentido().equals(getSentido());
    }

}
