package br.ucb.prevejo.transporte.percursoparada;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PercursoParadaID implements Serializable {

    @EqualsAndHashCode.Include
    @Column(name = "id_parada")
    private Integer idParada;

    @EqualsAndHashCode.Include
    @Column(name = "id_percurso")
    private Integer idPercurso;

    @EqualsAndHashCode.Include
    @Column(name = "sequencial")
    private Integer idSeq;
}
