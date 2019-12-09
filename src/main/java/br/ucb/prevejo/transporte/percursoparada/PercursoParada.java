package br.ucb.prevejo.transporte.percursoparada;

import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.percurso.Percurso;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="tb_percurso_parada", schema="transporte")
public class PercursoParada {

    @EqualsAndHashCode.Include
    @EmbeddedId
    private PercursoParadaID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parada", nullable = false, insertable = false, updatable = false)
    private Parada parada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_percurso", nullable = false, insertable = false, updatable = false)
    private Percurso percurso;

    @Column(name = "sequencial", nullable = false, insertable = false, updatable = false)
    private Integer sequencial;

}
