package br.ucb.prevejo.shared.serializer;

import br.ucb.prevejo.shared.util.Geo;
import br.ucb.prevejo.transporte.percurso.Percurso;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PercursoServiceRequestSerializer extends StdSerializer<Percurso> {

    public PercursoServiceRequestSerializer() {
        super(Percurso.class);
    }

    @Override
    public void serialize(Percurso percurso, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        JavaType javaType = serializerProvider.constructType(Percurso.class);
        BeanDescription beanDesc = serializerProvider.getConfig().introspect(javaType);
        JsonSerializer<Object> serializer = BeanSerializerFactory.instance.findBeanSerializer(serializerProvider, javaType, beanDesc);

        serializer.unwrappingSerializer(null).serialize(percurso, jsonGenerator, serializerProvider);

        jsonGenerator.writeFieldName("geo");
        jsonGenerator.writeRawValue(Geo.toGeoJson(percurso.getGeo()));

        jsonGenerator.writeEndObject();
    }

}
