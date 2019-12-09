package br.ucb.prevejo.comunidade.topico;

import br.ucb.prevejo.comunidade.comentario.Comentario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name="tb_topico", schema="comunidade")
@SequenceGenerator(name="seq_tb_topico", sequenceName="comunidade.seq_tb_topico", schema="comunidade")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_topico")
    private Integer id;

    @Column(name = "titulo", length = 255, nullable = false)
    private String titulo;

    @JsonIgnore
    @OneToMany(mappedBy = "topico", fetch = FetchType.LAZY)
    private Collection<Comentario> comentarios;

    protected Topico() {
    }

}
