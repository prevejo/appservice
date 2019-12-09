package br.ucb.prevejo.core;

import br.ucb.prevejo.shared.util.DateAndTime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class TimeSerializer extends StdSerializer<LocalDateTime> {

    public TimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeRawValue(String.valueOf(DateAndTime.toEpochMilli(localDateTime)));
    }

}
