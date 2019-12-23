package br.ucb.prevejo.shared.model;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.util.Collections;
import br.ucb.prevejo.shared.util.Geo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class LineStringSpliter {

    private LineString percurso;

    public Optional<FeatureCollection> split(LocatedEntity splitPoint, int maxDistance) {
        List<Point> percursoPoints = Arrays.asList(percurso.getCoordinates())
                .stream().map(coord -> Geo.makePoint(coord, percurso.getFactory()))
                .collect(Collectors.toList());

        Pair<List<Point>, List<Integer>> withinStartPoint = Geo.findPointsWithin(percursoPoints, splitPoint.getLocation(), maxDistance);

        List<Point> coordsPoints = withinStartPoint.getKey();
        List<Coordinate> coords = coordsPoints.stream().map(p -> p.getCoordinate()).collect(Collectors.toList());
        List<Integer> listOfNears = withinStartPoint.getValue();

        if (splitPoint.getRecordPath().size() >= 2) {
            List<Point> pointsFromStart = splitPoint.getRecordPath();
            LineString lineStrFromStart = Geo.toLineString(pointsFromStart, percurso.getFactory());
            double pathDistance = Geo.distance(pointsFromStart.iterator()).get();

            listOfNears = listOfNears.stream()
                    .filter(pathStart -> isPathDirectionValid(coordsPoints.subList(0, pathStart + 1), lineStrFromStart, pathDistance))
                    .collect(Collectors.toList());
        }

        return listOfNears.stream()
                .map(startIndex -> coords.subList(startIndex, coords.size()))
                .map(subPath -> Pair.of(subPath, Geo.distanceCoords(subPath.iterator())))
                .filter(pair -> pair.getValue().isPresent())
                .min(Comparator.comparingDouble(p -> p.getValue().get()))
                .map(pair -> Geo.splitLineString(coords, pair.getKey()));
    }

    public Optional<FeatureCollection> split(LocatedEntity startPoint, LocatedEntity endPoint, int maxDistance) {
        List<Point> percursoPoints = Arrays.asList(percurso.getCoordinates())
                .stream().map(coord -> Geo.makePoint(coord, percurso.getFactory()))
                .collect(Collectors.toList());

        Pair<List<Point>, List<Integer>> withinStartPoint = Geo.findPointsWithin(percursoPoints, startPoint.getLocation(), maxDistance);

        List<Point> coordsPoints = withinStartPoint.getKey();
        List<Integer> listOfNears = withinStartPoint.getValue();

        if (startPoint.getRecordPath().size() >= 2) {
            List<Point> pointsFromStart = startPoint.getRecordPath();
            LineString lineStrFromStart = Geo.toLineString(pointsFromStart, percurso.getFactory());
            double pathDistance = Geo.distance(pointsFromStart.iterator()).get();

            listOfNears = listOfNears.stream()
                    .filter(pathStart -> isPathDirectionValid(coordsPoints.subList(0, pathStart + 1), lineStrFromStart, pathDistance))
                    .collect(Collectors.toList());
        }

        return listOfNears.stream()
                .map(pathStartIndex -> buildSubPath(coordsPoints, pathStartIndex, endPoint.getLocation(), maxDistance))
                .filter(subPath -> subPath != null)
                .min(Comparator.comparingDouble(subPath -> subPath.distance))
                .map(subPath -> createSubPathFeatureCollection(subPath));
    }

    private boolean isPathDirectionValid(List<Point> path, LineString lineStr, double pathDistance) {
        List<Point> beforePath = Geo.maxDistanceWithCut(Collections.toList(Collections.reserveIterator(path)), (int) pathDistance);

        if (beforePath.size() < 2) {
            return false;
        }

        LineString lineStrPath = Geo.toLineString(beforePath, lineStr.getFactory());

        double distanceFromStart = DiscreteHausdorffDistance.distance(lineStrPath, lineStr) * 1000;

        return distanceFromStart < 3;
    }

    private SubPath buildSubPath(List<Point> path, int pathStartIndex, Point pathEnd, int maxDistance) {
        List<Point> pathBeforeStart = path.subList(0, pathStartIndex);
        List<Point> pathAfterStart = path.subList(pathStartIndex, path.size());

        Pair<List<Point>, List<Integer>> pointsWithin = Geo.findPointsWithin(pathAfterStart, pathEnd, maxDistance);

        pathAfterStart = pointsWithin.getKey();

        OptionalInt pathEndIndex = pointsWithin.getValue().stream().mapToInt(i -> i.intValue()).min();

        if (!pathEndIndex.isPresent()) {
            return null;
        }

        List<Point> pathAfterEnd = pathAfterStart.subList(pathEndIndex.getAsInt() + 1, pathAfterStart.size());

        List<Point> subPath = pathAfterStart.subList(0, pathEndIndex.getAsInt() + 1);
        List<Point> completePath = Stream.concat(Stream.concat(pathBeforeStart.stream(), subPath.stream()), pathAfterEnd.stream())
                .collect(Collectors.toList());

        Optional<Double> distance = Geo.distance(subPath.iterator());

        if (!distance.isPresent()) {
            return null;
        }

        return new SubPath(
                subPath.stream().map(p -> p.getCoordinate()).collect(Collectors.toList()),
                completePath.stream().map(p -> p.getCoordinate()).collect(Collectors.toList()),
                distance.get()
        );
    }

    private FeatureCollection createSubPathFeatureCollection(SubPath subPath) {
        return FeatureCollection.build(
                Geo.splitLineString(subPath.getCompletePath(), subPath.getSubPath())
                        .getFeatures().stream()
                        .filter(f -> !"end".equals(f.getProperties().get("position")))
                        .toArray(Feature[]::new)
        );
    }

    public static LineStringSpliter of(LineString percurso) {
        return new LineStringSpliter(percurso);
    }


    @Getter
    @AllArgsConstructor
    private class SubPath {
        private List<Coordinate> subPath;
        private List<Coordinate> completePath;
        private double distance;
    }

}

