package br.ucb.prevejo.comunidade.informativo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="tb_informativo", schema="comunidade")
@SequenceGenerator(name="seq_tb_informativo", sequenceName="comunidade.seq_tb_informativo", schema="comunidade", allocationSize = 1)
public class Informativo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_informativo")
    private Integer id;

    @Column(name = "titulo", length = 255, nullable = false)
    private String titulo;

    @Column(name = "resumo", length = 4000, nullable = false)
    private String resumo;

    @Column(name = "dt_publicacao", nullable = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dtPublicacao;

    @Column(name = "endereco", length = 255, nullable = false)
    private String endereco;

    private Informativo() {
    }

    public Informativo(String titulo, String resumo, LocalDateTime dtPublicacao, String endereco) {
        this.titulo = titulo;
        this.resumo = resumo;
        this.dtPublicacao = dtPublicacao;
        this.endereco = endereco;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public LocalDateTime getDtPublicacao() {
        return dtPublicacao;
    }

    public String getEndereco() {
        return endereco;
    }

    public InformativoDTO toDTO() {
        return new InformativoDTO(id, titulo, resumo, dtPublicacao, endereco);
    }

}
