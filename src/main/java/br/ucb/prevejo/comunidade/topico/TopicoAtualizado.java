package br.ucb.prevejo.comunidade.topico;

import br.ucb.prevejo.comunidade.comentario.ComentarioDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class TopicoAtualizado {

    private TopicoDTO topico;
    private Collection<ComentarioDTO> ultimosComentarios;

}
