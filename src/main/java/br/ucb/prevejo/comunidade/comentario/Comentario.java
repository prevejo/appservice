package br.ucb.prevejo.comunidade.comentario;

import br.ucb.prevejo.comunidade.topico.Topico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="tb_comentario", schema="comunidade")
@SequenceGenerator(name="seq_tb_comentario", sequenceName="comunidade.seq_tb_comentario", schema="comunidade", allocationSize = 1)
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_comentario")
    private Integer id;

    @Column(name = "assunto", length = 255, nullable = false)
    private String assunto;

    @Column(name = "comentario", length = 4000, nullable = false)
    private String comentario;

    @Column(name = "relevancia", nullable = false)
    private Integer relevancia;

    @Column(name = "dt_publicacao", nullable = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dtPublicacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topico", nullable = false)
    private Topico topico;

    protected Comentario() {
    }

    public Comentario(String assunto, String comentario, LocalDateTime dtPublicacao, Topico topico) {
        this.assunto = assunto;
        this.comentario = comentario;
        this.dtPublicacao = dtPublicacao;
        this.topico = topico;
        this.relevancia = 0;
    }

    public ComentarioDTO toDTO() {
        return new ComentarioDTO(id, assunto, comentario, relevancia, dtPublicacao);
    }

}
