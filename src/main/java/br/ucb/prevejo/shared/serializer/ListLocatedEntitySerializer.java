package br.ucb.prevejo.shared.serializer;

import br.ucb.prevejo.shared.model.ListLocatedEntity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class ListLocatedEntitySerializer extends StdSerializer<ListLocatedEntity> {

    public ListLocatedEntitySerializer() {
        super(ListLocatedEntity.class);
    }

    @Override
    public void serialize(ListLocatedEntity listLocatedEntity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        for (Point point : listLocatedEntity.getRecordPath()) {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeNumber(point.getX());
            jsonGenerator.writeNumber(point.getY());
            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndArray();
    }

}
