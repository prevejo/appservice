package br.ucb.prevejo.transporte.horario;

import br.ucb.prevejo.transporte.operacao.Operacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name="tb_horario", schema="transporte")
@SequenceGenerator(name="seq_tb_horario", sequenceName="transporte.seq_tb_horario", schema="transporte")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_horario")
    @EqualsAndHashCode.Include
    private Integer id;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operacao", nullable = false)
    private Operacao operacao;

    private Horario() {
    }

    public Horario(LocalTime horario) {
        this.horario = horario;
    }

    public LocalTime getHorario() {
        return horario;
    }

}
