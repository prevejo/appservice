package br.ucb.prevejo.shared.model;

import br.ucb.prevejo.shared.intefaces.Segment;
import br.ucb.prevejo.shared.util.Geo;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;

public class PointSegment implements Segment<Point> {

    private Point start;
    private Point end;
    private LineSegment segment;

    public PointSegment(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.segment = new LineSegment(getCoordinateInSegmentCRS(start), getCoordinateInSegmentCRS(end));
    }

    @Override
    public Point getSegmentStart() {
        return start;
    }

    @Override
    public Point getSegmentEnd() {
        return end;
    }

    @Override
    public double distance(Point point) {
        Coordinate coord = getCoordinateInSegmentCRS(point);

        return segment.distance(coord);
    }

    @Override
    public Point middlePoint(Point point) {
        Coordinate coord = getCoordinateInSegmentCRS(point);

        double fraction = segment.segmentFraction(coord);

        if (fraction == 0) {
            return start;
        } else if (fraction == 1) {
            return end;
        }

        Coordinate middleCoord = Geo.transform(
                segment.pointAlong(fraction),
                Geo.CRS_BRAZIL,
                Geo.findCoordinateReferenceSystem(start.getSRID()).get()
        );

        return start.getFactory().createPoint(middleCoord);
    }

    private static Coordinate getCoordinateInSegmentCRS(Point point) {
        return Geo.transform(point.getCoordinate(), Geo.findCoordinateReferenceSystem(point.getSRID()).get(), Geo.CRS_BRAZIL);
    }
}
