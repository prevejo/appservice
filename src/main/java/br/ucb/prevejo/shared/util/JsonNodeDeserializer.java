package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.places.search.PlaceSearchResult;
import br.ucb.prevejo.shared.exceptions.ParseException;
import br.ucb.prevejo.shared.intefaces.Parser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class JsonNodeDeserializer extends StdDeserializer implements Parser<String, JsonNode> {

    private ObjectMapper mapper = new ObjectMapper();
    private SimpleModule module = new SimpleModule();

    public JsonNodeDeserializer() {
        super(PlaceSearchResult.class);
        module.addDeserializer(Object.class, this);
        mapper.registerModule(module);
    }

    @Override
    public JsonNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return jsonParser.getCodec().readTree(jsonParser);
    }

    @Override
    public JsonNode parse(String str) throws ParseException {
        try {
            return (JsonNode) mapper.readValue(str, Object.class);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

}
