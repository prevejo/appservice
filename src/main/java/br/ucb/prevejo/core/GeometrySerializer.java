package br.ucb.prevejo.core;

import br.ucb.prevejo.shared.util.Geo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Geometry;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GeometrySerializer extends StdSerializer<Geometry> {

    public GeometrySerializer() {
        super(Geometry.class);
    }

    @Override
    public void serialize(Geometry geometry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeRawValue(Geo.toGeoJson(geometry));
    }

}
