package br.ucb.prevejo.transporte.linha;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Table(name="tb_linha", schema="transporte")
@SequenceGenerator(name="seq_tb_linha", sequenceName="transporte.seq_tb_linha", schema="transporte")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Linha {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_linha")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "numero", length = 5, nullable = false)
    private String numero;

    @Column(name = "descricao", length = 255, nullable = false)
    private String descricao;

    @Column(name = "tarifa", nullable = false)
    private BigDecimal tarifa;

    protected Linha() {
    }

    public Linha(String numero, String descricao, BigDecimal tarifa) {
        this.numero = numero;
        this.descricao = descricao;
        this.tarifa = tarifa;
    }
}
