package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.transporte.linha.Linha;
import br.ucb.prevejo.transporte.parada.Parada;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.locationtech.jts.geom.LineString;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Table(name="tb_percurso", schema="transporte")
@SequenceGenerator(name="seq_tb_percurso", sequenceName="transporte.seq_tb_percurso", schema="transporte")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Percurso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_percurso")
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_linha", nullable = false)
    private Linha linha;

    @Column(name = "sentido", length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumSentido sentido;

    @Column(name = "origem", length = 255, nullable = false)
    private String origem;

    @Column(name = "destino", length = 255, nullable = false)
    private String destino;

    @JsonIgnore
    @Column(name = "geo", nullable = false, columnDefinition = "geometry(LineString,4326)")
    private LineString geo;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "transporte", name = "tb_percurso_parada", joinColumns = {
            @JoinColumn(name = "id_percurso")
    }, inverseJoinColumns = {
            @JoinColumn(name = "id_parada")
    })
    private Collection<Parada> paradas;

    private Percurso() {
    }

    public Percurso(EnumSentido sentido, String origem, String destino, LineString geo) {
        this.sentido = sentido;
        this.origem = origem;
        this.destino = destino;
        this.geo = geo;
    }

    public EnumSentido getSentido() {
        return sentido;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public LineString getGeo() {
        return geo;
    }

    @Transient
    @JsonIgnore
    public PercursoDTO toDTO() {
        return new PercursoDTOImpl(getId(), getSentido(), getLinha(), getOrigem(), getDestino());
    }

}
