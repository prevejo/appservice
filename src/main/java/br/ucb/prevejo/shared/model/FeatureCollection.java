package br.ucb.prevejo.shared.model;

import lombok.Getter;
import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class FeatureCollection {

    private String type = "FeatureCollection";
    private Collection<Feature> features;

    private FeatureCollection(Collection<Feature> features) {
        this.features = features;
    }

    public static <T> FeatureCollection build(Feature ...features) {
        return new FeatureCollection(Arrays.asList(features));
    }

    public static <T> FeatureCollection build(Collection<T> features, Function<T, Geometry> geometrySupplier, Function<T, Map<String, Object>> propsSupplier) {
        return new FeatureCollection(
                features.stream()
                        .map(t -> Feature.build(geometrySupplier.apply(t), propsSupplier.apply(t)))
                        .collect(Collectors.toList())
        );
    }

}
