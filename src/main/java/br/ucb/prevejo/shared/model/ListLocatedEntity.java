package br.ucb.prevejo.shared.model;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

import java.util.List;

public class ListLocatedEntity implements LocatedEntity {

    private List<Point> path;

    public ListLocatedEntity(List<Point> path) {
        this.path = path;
    }

    @Override
    public Point getLocation() {
        return path.get(path.size() - 1);
    }

    @JsonIgnore
    @Override
    public List<Point> getRecordPath() {
        return path;
    }

}
