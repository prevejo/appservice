package br.ucb.prevejo.comunidade.comentario;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ComentarioDTO {

    private Integer id;
    private String assunto;
    private String comentario;
    private Integer relevancia;
    private LocalDateTime dtPublicacao;

}
