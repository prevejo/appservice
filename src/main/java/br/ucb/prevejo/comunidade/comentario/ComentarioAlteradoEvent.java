package br.ucb.prevejo.comunidade.comentario;

import org.springframework.context.ApplicationEvent;

public class ComentarioAlteradoEvent extends ApplicationEvent {

    private Comentario comentario;

    public ComentarioAlteradoEvent(Object source, Comentario comentario) {
        super(source);
        this.comentario = comentario;
    }

    public Comentario getComentario() {
        return comentario;
    }

}
