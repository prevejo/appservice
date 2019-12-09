package br.ucb.prevejo.shared.model;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SequencedPoint implements Comparable<SequencedPoint>, LocatedEntity {

    @EqualsAndHashCode.Include
    private Integer sequence;
    private Point point;

    @Override
    public Point getLocation() {
        return getPoint();
    }

    @Override
    public int compareTo(SequencedPoint sequencedPoint) {
        return getSequence().compareTo(sequencedPoint.getSequence());
    }
}
