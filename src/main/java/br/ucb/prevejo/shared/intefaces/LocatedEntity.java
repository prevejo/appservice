package br.ucb.prevejo.shared.intefaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

import java.util.Collections;
import java.util.List;

public interface LocatedEntity {
    Point getLocation();

    @JsonIgnore
    default List<Point> getRecordPath() {
        return Collections.emptyList();
    }
}
