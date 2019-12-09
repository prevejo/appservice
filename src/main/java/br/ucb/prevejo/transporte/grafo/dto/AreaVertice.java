package br.ucb.prevejo.transporte.grafo.dto;

import br.ucb.prevejo.transporte.grafo.model.VerticeImpl;
import lombok.EqualsAndHashCode;;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AreaVertice extends VerticeImpl<ArestaLigacao> {

    @EqualsAndHashCode.Include
    private Area area;

    public AreaVertice(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

    @Override
    public String toString() {
        return area.toString();
    }

}
