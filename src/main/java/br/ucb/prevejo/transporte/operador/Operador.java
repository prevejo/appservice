package br.ucb.prevejo.transporte.operador;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
@Table(name="tb_operador", schema="transporte")
@SequenceGenerator(name="seq_tb_operador", sequenceName="transporte.seq_tb_operador", schema="transporte")
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_operador")
    private Integer id;

    @Column(name = "descricao", length = 255, nullable = false)
    private String descricao;

    private Operador() {
    }

    public Operador(String descricao) {
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonSerialize(using = OperadorShortDescriptionSerializer.class)
    @Transient
    public String getDescricaoShort() {
        return getDescricao();
    }

}
