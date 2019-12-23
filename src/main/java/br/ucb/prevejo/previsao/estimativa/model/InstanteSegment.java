package br.ucb.prevejo.previsao.estimativa.model;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.shared.intefaces.Segment;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class InstanteSegment implements Segment<Instante> {

    private Instante instanteInicial;
    private Instante instanteFinal;
    private LineSegment segmento;

    private InstanteSegment(Instante instanteInicial, Instante instanteFinal, LineSegment segmento) {
        this.instanteInicial = instanteInicial;
        this.instanteFinal = instanteFinal;
        this.segmento = segmento;
    }

    @Override
    public Instante getSegmentStart() {
        return instanteInicial;
    }

    @Override
    public Instante getSegmentEnd() {
        return instanteFinal;
    }

    @Override
    public double distance(Point point) {
        Coordinate coord = getCoordinateInSegmentCRS(point);

        return segmento.distance(coord);
    }

    @Override
    public Instante middlePoint(Point point) {
        Coordinate coord = getCoordinateInSegmentCRS(point);

        double fraction = segmento.segmentFraction(coord);

        if (fraction == 0) {
            return instanteInicial;
        } else if (fraction == 1) {
            return instanteFinal;
        }

        LocalDateTime middleTime = DateAndTime.middleTime(instanteInicial.getData(), instanteFinal.getData(), fraction);

        if (middleTime.equals(instanteInicial.getData())) {
            return instanteInicial;
        } else if (middleTime.equals(instanteFinal.getData())) {
            return instanteFinal;
        }

        Coordinate middleCoord = Geo.transform(
                segmento.pointAlong(fraction),
                Geo.CRS_BRAZIL,
                Geo.findCoordinateReferenceSystem(instanteInicial.getLocalizacao().getSRID()).get()
        );

        return new Instante(middleTime, instanteInicial.getLocalizacao().getFactory().createPoint(middleCoord));
    }

    public static InstanteSegment build(Instante instanteInicial, Instante instanteFinal) {
        Coordinate coordInicial = getCoordinateInSegmentCRS(instanteInicial.getLocalizacao());
        Coordinate coordFinal = getCoordinateInSegmentCRS(instanteFinal.getLocalizacao());

        return new InstanteSegment(instanteInicial, instanteFinal, new LineSegment(coordInicial, coordFinal));
    }

    private static Coordinate getCoordinateInSegmentCRS(Point point) {
        return Geo.transform(point.getCoordinate(), Geo.findCoordinateReferenceSystem(point.getSRID()).get(), Geo.CRS_BRAZIL);
    }

}
