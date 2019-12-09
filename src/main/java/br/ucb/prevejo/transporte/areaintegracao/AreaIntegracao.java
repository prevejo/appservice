package br.ucb.prevejo.transporte.areaintegracao;

import br.ucb.prevejo.transporte.parada.Parada;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.locationtech.jts.geom.MultiPolygon;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@NamedEntityGraph(name = "AreaIntegracao.paradas", attributeNodes = @NamedAttributeNode("paradas"))
@Table(name="tb_area_integracao", schema="transporte")
@SequenceGenerator(name="seq_tb_area_integracao", sequenceName="transporte.seq_tb_area_integracao", schema="transporte")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AreaIntegracao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_area_integracao")
    @EqualsAndHashCode.Include
    private Integer id;

    @ToString.Include
    @Column(name = "descricao", length = 255, nullable = false)
    private String descricao;

    @JsonIgnore
    @Column(name = "geo", nullable = false, columnDefinition = "geometry(MultiPolygon,4326)")
    private MultiPolygon geo;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "transporte", name = "tb_area_parada", joinColumns = {
            @JoinColumn(name = "id_area_integracao")
    }, inverseJoinColumns = {
            @JoinColumn(name = "id_parada")
    })
    private Collection<Parada> paradas;

    private AreaIntegracao() {
    }

}
