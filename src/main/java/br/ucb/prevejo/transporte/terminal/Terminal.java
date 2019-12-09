package br.ucb.prevejo.transporte.terminal;

import br.ucb.prevejo.transporte.parada.Parada;
import lombok.Getter;
import org.locationtech.jts.geom.Polygon;

import javax.persistence.*;

@Entity
@Getter
@Table(name="tb_terminal", schema="transporte")
@SequenceGenerator(name="seq_tb_terminal", sequenceName="transporte.seq_tb_terminal", schema="transporte")
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_terminal")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_parada", nullable = false)
    private Parada parada;

    @Column(name = "cod", length = 5, nullable = false)
    private String cod;

    @Column(name = "descricao", length = 255, nullable = false)
    private String descricao;

    @Column(name = "geo", nullable = false, columnDefinition = "geometry(Polygon,4326)")
    private Polygon geo;

    private Terminal() {
    }

}
