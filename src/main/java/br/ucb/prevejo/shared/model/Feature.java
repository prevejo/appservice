package br.ucb.prevejo.shared.model;

import lombok.Getter;
import org.locationtech.jts.geom.Geometry;

import java.util.Map;

@Getter
public class Feature {

    private String type = "Feature";
    private Geometry geometry;
    private Map<String, Object> properties;

    private Feature(Geometry geometry, Map<String, Object> properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public static Feature build(Geometry geometry, Map<String, Object> properties) {
        return new Feature(geometry, properties);
    }

}
