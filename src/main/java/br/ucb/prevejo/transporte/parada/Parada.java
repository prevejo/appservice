package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

@Entity
@Table(name="tb_parada", schema="transporte")
@SequenceGenerator(name="seq_tb_parada", sequenceName="transporte.seq_tb_parada", schema="transporte")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Parada implements LocatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_parada")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "cod", length = 5, nullable = false)
    private String cod;

    @Column(name = "geo", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point geo;

    @JsonIgnore
    @Column(name = "geo_via", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point geoVia;

    private Parada() {
    }

    public ParadaDTO toDto() {
        return new ParadaDTOImpl(id, cod, geo);
    }

    @JsonIgnore
    @Override
    public Point getLocation() {
        return getGeo();
    }
}
