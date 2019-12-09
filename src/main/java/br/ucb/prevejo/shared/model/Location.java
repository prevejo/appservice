package br.ucb.prevejo.shared.model;

import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Getter
public class Location {

    private BigDecimal lat;
    private BigDecimal lng;

    private Location(BigDecimal lat, BigDecimal lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public static Location build(String lat, String lng) {
        try {
            return build(new BigDecimal(lat), new BigDecimal(lng));
        } catch(NumberFormatException e) {
            throw new RuntimeException("Entrada inválida");
        }
    }

    public static Location build(BigDecimal lat, BigDecimal lng) {
        return new Location(lat, lng);
    }

    public static Location build(Point point) {
        return build(BigDecimal.valueOf(point.getY()), BigDecimal.valueOf(point.getX()));
    }

}
