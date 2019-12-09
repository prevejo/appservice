package br.ucb.prevejo.shared.intefaces;

import org.locationtech.jts.geom.Point;

public interface DistanceBetween extends Comparable<DistanceBetween> {

    public double getDistance();
    public Point getFirstPoint();
    public Point getSecondPoint();

    @Override
    default int compareTo(DistanceBetween atDistance) {
        return Double.compare(getDistance(), atDistance.getDistance());
    }

}
