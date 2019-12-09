package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.previsao.estimativa.model.AtDistance;
import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.shared.model.Pair;
import br.ucb.prevejo.transporte.percurso.Percurso;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Geo {

    public static final CoordinateReferenceSystem CRS_GOOGLE = DefaultGeographicCRS.WGS84;
    private static final GeometryJSON GEOJSON_BUILDER = new GeometryJSON(5);

    private static final GeometryFactory FACTORY_WGS84 = new GeometryFactory(new PrecisionModel((int)Math.pow(10, 8)), 4326);

    private static final Map<Integer, CoordinateReferenceSystem> SRID_MAP = new HashMap<Integer, CoordinateReferenceSystem>() {{
        put(4326, CRS_GOOGLE);
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

    public static String toGeoJson(Geometry geometry) {
        return GEOJSON_BUILDER.toString(geometry);
    }

    public static LineString toLineString(Coordinate[] coordinates, GeometryFactory factory) {
        return factory.createLineString(coordinates);
    }

    public static LineString toLineString(List<Point> points) {
        return toLineString(points.stream().map(p -> p.getCoordinate()).toArray(Coordinate[]::new), FACTORY_WGS84);
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

    public static Point makePointXY(double x, double y) {
        return FACTORY_WGS84.createPoint(new Coordinate(x, y));
    }

    public static Point makePoint(String lat, String lng) {
        return makePoint(Double.valueOf(lat.replace(",", ".")), Double.valueOf(lng.replace(",", ".")));
    }


    public static List<Point> fillSequence(List<Point> points, int distance) {
        List<Pair<Point, Point>> list = br.ucb.prevejo.shared.util.Collections.pairIteratorOf(points.iterator()).toSequencePairList();

        Stream<Point> firstPoint = list.stream()
                .findFirst()
                .map(pair -> Stream.of(pair.getKey()))
                .orElse(Stream.empty());

        Stream<Point> otherPoints = list.stream()
                .flatMap(pair -> Stream.concat(
                        middlePoints(pair.getKey(), pair.getValue(), distance),
                        Arrays.asList(pair.getValue()).stream()
                ));

        return Stream.concat(firstPoint, otherPoints).collect(Collectors.toList());
    }

    private static Stream<Point> middlePoints(Point first, Point second, int distance) {
        List<Point> points = makePointsBetween(first, second, distance);

        Stream<Point> middleOnes = points.size() > 2
                ? points.subList(1, points.size() - 1).stream()
                : Stream.empty();

        return middleOnes;
    }

    public static List<Point> maxDistance(Iterator<Point> iterator, int maxDistance) {
        return maxDistanceCoords(iterator, maxDistance, p -> p.getCoordinate());
    }

    public static List<Coordinate> maxDistanceCoords(Iterator<Coordinate> iterator, int maxDistance) {
        return maxDistanceCoords(iterator, maxDistance, c -> c);
    }

    public static <T> List<T> maxDistanceCoords(Iterator<T> iterator, int maxDistance, Function<T, Coordinate> coordinateGetter) {
        List<T> points = new ArrayList<>();
        double totalDistance = 0;

        while (iterator.hasNext()) {
            T next = iterator.next();
            Coordinate nextCoord = coordinateGetter.apply(next);

            if (!points.isEmpty()) {
                Coordinate previous = coordinateGetter.apply(points.get(points.size() - 1));

                totalDistance += distanceBetween(previous, nextCoord);

                if (totalDistance > maxDistance) {
                    break;
                }
            }

            points.add(next);
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

    public static List<Integer> findNearOnes(List<Coordinate> coordinates, Point point, int maxDistance) {
        List<Coordinate> coords = new ArrayList<>(coordinates);
        SortedSet<AtDistance<Integer>> nearBag = new TreeSet<>();
        boolean entryFlag = false;

        List<Integer> listOfNears = new ArrayList<>();

        for (int i = 0; i < coords.size(); i++) {
            double distance = Geo.distanceBetween(coords.get(i), point.getCoordinate());

            if (distance <= maxDistance) {
                entryFlag = true;

                nearBag.add(AtDistance.build(i, distance));
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

    public static Optional<FeatureCollection> splitLineString(LocatedEntity splitPoint, LineString lineString, int maxDistance) {
        List<Coordinate> coords = Geo.fillSequence(Arrays.asList(lineString.getCoordinates())
                .stream().map(c -> Geo.makePoint(c))
                .collect(Collectors.toList()), 50)
                .stream().map(p -> p.getCoordinate())
                .collect(Collectors.toList());

        List<Integer> listOfNears = Geo.findNearOnes(coords, splitPoint.getLocation(), maxDistance)
                .stream().filter(index -> index < coords.size() - 1)
                .collect(Collectors.toList());

        if (splitPoint.getRecordPath().size() >= 2 && listOfNears.size() > 1) {
            List<Point> pointsFromStart = Geo.fillSequence(splitPoint.getRecordPath(), 10);
            LineString lineStrFromStart = Geo.toLineString(pointsFromStart);
            double pathDistance = Geo.distance(splitPoint.getRecordPath().iterator()).get();

            listOfNears = listOfNears.stream()
                    .filter(pathStart -> {
                        Iterator<Coordinate> beforePath = Collections.reserveIterator(coords.subList(0, pathStart + 1));
                        List<Coordinate> path = Geo.maxDistanceCoords(beforePath, (int) pathDistance);

                        if (path.size() < 2) {
                            return false;
                        }

                        LineString lineStrPath = Geo.toLineStringCoords(path);

                        double distanceFromStart = DiscreteHausdorffDistance.distance(lineStrPath, lineStrFromStart);

                        return distanceFromStart * 1000 < 3;
                    }).collect(Collectors.toList());
        }

        return listOfNears.stream()
                .map(pathStart -> coords.subList(pathStart, coords.size()))
                .map(path -> Pair.of(path, Geo.distanceCoords(path.iterator())))
                .filter(pair -> pair.getValue().isPresent())
                .min(Comparator.comparingDouble(p -> p.getValue().get()))
                .map(pair -> Geo.splitLineString(coords, pair.getKey()));
    }

}
