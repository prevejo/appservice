package br.ucb.prevejo.comunidade.comentario;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class NovoComentarioDTO {

    @NotNull(message = "Topico não informado")
    private Integer topicoId;

    @NotBlank(message = "Assunto vazio")
    @Size(min = 2, max = 255, message = "Assunto possuí tamanho fora do limite [2, 255]")
    private String assunto;

    @NotBlank(message = "Comentário vazio")
    @Size(min = 2, max = 4000, message = "Comentário possuí tamanho fora do limite [2, 4000]")
    private String comentario;

}
