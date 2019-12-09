package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.transporte.linha.Linha;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PercursoDTOImpl implements PercursoDTO {

    @EqualsAndHashCode.Include
    private Integer id;
    private EnumSentido sentido;
    private Linha linha;
    private String origem;
    private String destino;

}
