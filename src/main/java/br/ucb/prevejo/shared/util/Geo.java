package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.previsao.estimativa.model.AtDistance;
import br.ucb.prevejo.shared.intefaces.Segment;
import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.shared.model.PointSegment;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.algorithm.Distance;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Geo {

    public static final CoordinateReferenceSystem CRS_GOOGLE = DefaultGeographicCRS.WGS84;
    public static final CoordinateReferenceSystem CRS_BRAZIL = buildCrs(31983);

    private static final GeometryJSON GEOJSON_BUILDER = new GeometryJSON(5);

    private static final GeometryFactory FACTORY_WGS84 = new GeometryFactory(new PrecisionModel((int)Math.pow(10, 8)), 4326);

    private static final Map<Integer, CoordinateReferenceSystem> SRID_MAP = new HashMap<Integer, CoordinateReferenceSystem>() {{
        put(4326, CRS_GOOGLE);
        put(31983, CRS_BRAZIL);
    }};

    public static Optional<Double> distance(Iterator<Point> points) {
        return distance(points, p -> p.getCoordinate());
    }

    public static Optional<Double> distanceCoords(Iterator<Coordinate> points) {
        return distance(points, p -> p);
    }

    public static <T> Optional<Double> distance(Iterator<T> points, Function<T, Coordinate> coordinateGetter) {
        Coordinate previousOne = null;
        double distance = -1;

        while (points.hasNext()) {
            Coordinate next = coordinateGetter.apply(points.next());

            if (previousOne != null) {
                if (distance == -1) {
                    distance = 0;
                }
                distance += distanceBetween(previousOne, next);
            }

            previousOne = next;
        }

        return distance == -1 ? Optional.empty() : Optional.of(Double.valueOf(distance));
    }

    public static double distanceBetween(Point pointA, Point pointB) {
        if (pointA.getSRID() != pointB.getSRID()) {
            throw new IllegalArgumentException("SRID of A is diferent of B");
        }

        return Optional.ofNullable(SRID_MAP.get(pointA.getSRID()))
            .map(crs -> distanceBetween(pointA, pointB, crs))
            .orElseThrow(() -> new IllegalArgumentException("The points doesn't have a known SRID"));
    }

    public static double distanceBetween(Point pointA, Point pointB, CoordinateReferenceSystem crs) {
        return distanceBetween(pointA.getCoordinate(), pointB.getCoordinate(), crs);
    }

    public static double distanceBetween(Coordinate pointA, Coordinate pointB) {
        return distanceBetween(pointA, pointB, null);
    }

    public static double distanceBetween(Coordinate pointA, Coordinate pointB, CoordinateReferenceSystem crs) {
        if (crs == null) {
            crs = CRS_GOOGLE;
        }

        try {
            return JTS.orthodromicDistance(pointA, pointB, crs);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }

    public static double distanceBetween(Point point, LineString line) {
        return distanceBetweenPointAndLine(point.getCoordinate(), line.getCoordinates());
    }

    public static double distanceBetweenPointAndLine(Coordinate point, Coordinate[] lineCoords) {
        return Distance.pointToSegmentString(point, lineCoords);
    }

    public static double distanceBetweenPointAndLine(Coordinate point, Coordinate lineStart, Coordinate lineEnd) {
        return Distance.pointToSegment(point, lineStart, lineEnd);
    }

    public static String toGeoJson(Geometry geometry) {
        return GEOJSON_BUILDER.toString(geometry);
    }

    public static LineString toLineString(Coordinate[] coordinates, GeometryFactory factory) {
        return factory.createLineString(coordinates);
    }

    public static LineString toLineString(List<Point> points) {
        return toLineString(points, FACTORY_WGS84);
    }

    public static LineString toLineString(List<Point> points, GeometryFactory factory) {
        return toLineString(points.stream().map(p -> p.getCoordinate()).toArray(Coordinate[]::new), factory);
    }

    public static LineString toLineStringCoords(List<Coordinate> points) {
        return toLineString(points.stream().toArray(Coordinate[]::new), FACTORY_WGS84);
    }

    public static List<Point> makePointsBetween(Point p1, Point p2, int distance) {
        int limit = 100000, count = 0;
        List<Point> points = Arrays.asList(p1, p2);

        if (distance > 0) {
            do {
                count++;
                int pointsDistance = (int) distanceBetween(points.get(0), points.get(1));

                if (pointsDistance <= distance) {
                    break;
                } else if (count == limit) {
                    throw new IllegalStateException("Iteration limit exced");
                }

                points = makePointsBetween(points.iterator());
            } while (true);
        }

        return points;
    }

    public static List<Point> makePointsBetween(Iterator<Point> points) {
        List<Pair<Point, Point>> list = Collections.pairIteratorOf(points).toSequencePairList();

        Stream<Point> firstPoint = list.stream()
                .findFirst()
                .map(pair -> Stream.of(pair.getKey()))
                .orElse(Stream.empty());

        Stream<Point> otherPoints = list.stream()
                .flatMap(pair -> Stream.of(
                        middlePoint(pair.getKey(), pair.getValue()),
                        pair.getValue()
                ));

        return Stream.concat(firstPoint, otherPoints).collect(Collectors.toList());
    }

    public static Point middlePoint(Point p1, Point p2) {
        Coordinate middleCoord = new Coordinate((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);

        return GeometryFactory.createPointFromInternalCoord(middleCoord, p1);
    }

    public static Point makePoint(double lat, double lng) {
        return makePoint(new Coordinate(lng, lat));
    }

    public static Point makePoint(Coordinate coordinate) {
        return FACTORY_WGS84.createPoint(coordinate);
    }

    public static Point makePoint(Coordinate coordinate, GeometryFactory factory) {
        return factory.createPoint(coordinate);
    }

    public static Point makePointXY(double x, double y) {
        return FACTORY_WGS84.createPoint(new Coordinate(x, y));
    }

    public static Point makePoint(String lat, String lng) {
        return makePoint(Double.valueOf(lat.replace(",", ".")), Double.valueOf(lng.replace(",", ".")));
    }

    public static Coordinate pointAt(double fraction, Coordinate lineStart, Coordinate lineEnd) {
        return new LineSegment(lineStart, lineEnd).pointAlong(fraction);
    }

    public static Coordinate perpendicularPoint(Coordinate point, Coordinate lineStart, Coordinate lineEnd) {
        double x1=lineStart.x, y1=lineStart.y, x2=lineEnd.x, y2=lineEnd.y, x3=point.x, y3=point.y;
        double px = x2-x1, py = y2-y1, dAB = px*px + py*py;
        double u = ((x3 - x1) * px + (y3 - y1) * py) / dAB;
        double x = x1 + u * px, y = y1 + u * py;
        return new Coordinate(x, y);
    }

    public static double closestPointFractionAt(Coordinate point, Coordinate lineStart, Coordinate lineEnd) {
        return new LineSegment(lineStart, lineEnd).segmentFraction(point);
    }

    public static Coordinate closestPoint(Coordinate point, Coordinate lineStart, Coordinate lineEnd) {
        return new LineSegment(lineStart, lineEnd).closestPoint(point);
    }

    private static Stream<Point> middlePoints(Point first, Point second, int distance) {
        List<Point> points = makePointsBetween(first, second, distance);

        Stream<Point> middleOnes = points.size() > 2
                ? points.subList(1, points.size() - 1).stream()
                : Stream.empty();

        return middleOnes;
    }

    public static List<Coordinate> maxDistanceWithCut(List<Coordinate> coords, int maxDistance, GeometryFactory factory, CoordinateReferenceSystem crs) {
        return maxDistanceWithCut(
                coords.iterator(),
                maxDistance,
                p -> p,
                coord -> factory.createPoint(coord).getCoordinate(),
                crs
        );
    }

    public static List<Point> maxDistanceWithCut(List<Point> points, int maxDistance) {
        if (points.isEmpty()) {
            return points;
        }

        Point first = points.get(0);

        return maxDistanceWithCut(
                points.iterator(),
                maxDistance,
                p -> p.getCoordinate(),
                coord -> first.getFactory().createPoint(coord),
                Geo.findCoordinateReferenceSystem(first.getSRID()).get()
        );
    }

    public static <T> List<T> maxDistanceWithCut(
            Iterator<T> iterator,
            int maxDistance,
            Function<T, Coordinate> toCoord,
            Function<Coordinate, T> toObj,
            CoordinateReferenceSystem crs) {

        List<T> points = new ArrayList<>();
        double totalDistance = 0;
        T prev = null;

        while (iterator.hasNext()) {
            T next = iterator.next();

            if (prev != null) {
                Coordinate prevCoord = toCoord.apply(prev);
                Coordinate nextCoord = toCoord.apply(next);

                double distance = distanceBetween(prevCoord, nextCoord, crs);

                if ((totalDistance + distance) > maxDistance) {
                    if (totalDistance < maxDistance) {
                        double remaining = maxDistance - totalDistance;
                        double remainingFraction = (remaining * 100 / distance) / 100;

                        points.add(toObj.apply(pointAt(remainingFraction, prevCoord, nextCoord)));
                    }
                    break;
                }
                totalDistance += distance;
            }

            points.add(next);
            prev = next;
        }

        return points;
    }

    public static Point closestPointInLine(Point lineStart, Point lineEnd, Point point) {
        LineString ls = Geo.toLineString(Arrays.asList(lineStart, lineEnd));

        double hipotenusa = lineStart.distance(point);
        double oposto = ls.distance(point);
        double adjacente = Math.sqrt(Math.pow(hipotenusa, 2) - Math.pow(oposto, 2));

        return pointInBetween(lineStart, lineEnd, adjacente);
    }

    public static FeatureCollection splitLineString(LineString line, List<Coordinate> coordinates) {
        if (coordinates.isEmpty()) {
            return FeatureCollection.build(Feature.build(line, new HashMap<String, Object>(){{
                put("position", "complete");
            }}));
        }

        List<Coordinate> lineCoords = Arrays.asList(line.getCoordinates());

        int startIndex = lineCoords.indexOf(coordinates.get(0));
        int endIndex = lineCoords.indexOf(coordinates.get(coordinates.size() - 1));

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new IllegalArgumentException("Invalid position");
        }

        return splitLineString(line, startIndex, endIndex);
    }

    public static FeatureCollection splitLineString(List<Coordinate> lineStringCoords, List<Coordinate> coordinates) {
        if (coordinates.isEmpty()) {
            return FeatureCollection.build(Feature.build(toLineStringCoords(lineStringCoords), new HashMap<String, Object>(){{
                put("position", "complete");
            }}));
        }

        int startIndex = lineStringCoords.indexOf(coordinates.get(0));
        int endIndex = lineStringCoords.lastIndexOf(coordinates.get(coordinates.size() - 1));

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new IllegalArgumentException("Invalid position");
        }

        return splitLineString(lineStringCoords, startIndex, endIndex);
    }

    public static FeatureCollection splitLineString(LineString line, int pathStart, int pathEnd) {
        return splitLineString(Arrays.asList(line.getCoordinates()), pathStart, pathEnd);
    }

    public static FeatureCollection splitLineString(List<Coordinate> lineStringCoords, int pathStart, int pathEnd) {
        List<Coordinate> coords = lineStringCoords;

        List<Coordinate> start = coords.subList(0, pathStart);
        List<Coordinate> middle = coords.subList(pathStart, pathEnd + 1);
        List<Coordinate> end = pathEnd < coords.size() - 1 ? coords.subList(pathEnd + 1, coords.size()) : java.util.Collections.emptyList();

        return FeatureCollection.build(
                Arrays.asList(
                        Pair.of("start", start),
                        Pair.of("middle", middle),
                        Pair.of("end", end)
                ).stream().filter(pair -> !pair.getValue().isEmpty() && pair.getValue().size() >= 2)
                        .map(pair -> Feature.build(toLineStringCoords(pair.getValue()), new HashMap<String, Object>() {{
                            put("position", pair.getKey());
                            put("distance", distanceCoords(pair.getValue().iterator()).orElse(Double.valueOf(0)));
                        }})).toArray(Feature[]::new)
        );
    }

    public static <T extends Segment> List<T> findSegmentsNear(Iterator<T> segments, Point point, int maxDistance) {
        List<T> listOfNears = new ArrayList<>();
        SortedSet<AtDistance<T>> nearBag = new TreeSet<>();
        boolean entryFlag = false;

        while (segments.hasNext()) {
            T ls = segments.next();
            double distance = ls.distance(point);

            if (distance <= maxDistance) {
                entryFlag = true;

                nearBag.add(AtDistance.build(ls, distance));
            } else {
                if (entryFlag) {
                    entryFlag = false;

                    listOfNears.add(nearBag.first().getEntity());
                    nearBag.clear();
                }
            }
        }

        return listOfNears;
    }

    private static Point pointInBetween(Point pointA, Point pointB, double distanceFromAtoBinDegrees) {
        double distance = pointB.distance(pointA);
        double f = (distance - distanceFromAtoBinDegrees) / distance;
        return Geo.makePoint(
                pointB.getY() + f * (pointA.getY() - pointB.getY()),
                pointB.getX() + f * (pointA.getX() - pointB.getX())
        );
    }

    public static Pair<List<Point>, List<Integer>> findPointsWithin(List<Point> points, Point point, int maxDistance) {
        List<PointSegment> segmentos = Collections.pairIteratorOf(points.iterator()).toSequencePairList()
                .stream().map(pair -> new PointSegment(pair.getKey(), pair.getValue())).collect(Collectors.toList());
        List<PointSegment> nearOnes = findSegmentsNear(segmentos.iterator(), point, maxDistance);

        List<Pair<Point, Boolean>> coordsPoints = Stream.concat(
            segmentos.stream().findFirst()
                    .map(seg -> Stream.concat(
                        Stream.of(Pair.of(seg.getSegmentStart(), false)),
                        (nearOnes.contains(seg) ? Stream.of(Pair.of(seg.middlePoint(point), true)) : Stream.empty())
                    )).orElse(Stream.empty()),
            segmentos.stream().skip(1).flatMap(seg -> Stream.concat(
                    (nearOnes.contains(seg) ? Stream.of(Pair.of(seg.middlePoint(point), true)) : Stream.empty()),
                    Stream.of(Pair.of(seg.getSegmentEnd(), false))
        ))).collect(Collectors.toList());

        List<Integer> listOfNears = new ArrayList<>();

        int index = 0;
        for (Pair<Point, Boolean> coordPoint : coordsPoints) {
            if (coordPoint.getValue()) {
                listOfNears.add(index);
            }
            index++;
        }

        List<Point> pointsExpanded = coordsPoints.stream().map(coordPoint -> coordPoint.getKey()).collect(Collectors.toList());

        return Pair.of(pointsExpanded, listOfNears);
    }

    public static Coordinate transform(Coordinate coordinate, CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        try {
            return JTS.transform(coordinate, new Coordinate(), getTransform(source, target));
        } catch (TransformException e) {
            throw new RuntimeException(e);
        }
    }

    private static CoordinateReferenceSystem buildCrs(int code) {
        try {
            return CRS.decode("EPSG:31983");
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private static MathTransform getTransform(CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        try {
            return CRS.findMathTransform(source, target, true);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<CoordinateReferenceSystem> findCoordinateReferenceSystem(int sridCode) {
        return Optional.ofNullable(SRID_MAP.get(sridCode));
    }

}
