package br.ucb.prevejo.comunidade.informativo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InformativoDTO {

    private Integer id;
    private String titulo;
    private String resumo;
    private LocalDateTime dtPublicacao;
    private String endereco;

}
