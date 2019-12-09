package br.ucb.prevejo.transporte.operacao;

import br.ucb.prevejo.transporte.horario.Horario;
import br.ucb.prevejo.transporte.operador.Operador;
import br.ucb.prevejo.transporte.percurso.Percurso;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Table(name="tb_operacao", schema="transporte")
@SequenceGenerator(name="seq_tb_operacao", sequenceName="transporte.seq_tb_operacao", schema="transporte")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Operacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_operacao")
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_operador", nullable = false)
    private Operador operador;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_percurso", nullable = false)
    private Percurso percurso;

    @Column(name = "segunda", nullable = false)
    private Boolean segunda;

    @Column(name = "terca", nullable = false)
    private Boolean terca;

    @Column(name = "quarta", nullable = false)
    private Boolean quarta;

    @Column(name = "quinta", nullable = false)
    private Boolean quinta;

    @Column(name = "sexta", nullable = false)
    private Boolean sexta;

    @Column(name = "sabado", nullable = false)
    private Boolean sabado;

    @Column(name = "domingo", nullable = false)
    private Boolean domingo;

    @OneToMany
    @JoinColumn(name = "id_operacao")
    private Collection<Horario> horarios;

    private Operacao() {
    }

}
