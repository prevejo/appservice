package br.ucb.prevejo.transporte.operador;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OperadorShortDescriptionSerializer extends JsonSerializer<String> {

    private Map<String, String> operadoresMap = new HashMap<String, String>() {{
        put("PIRACICABANA", "Piracicabana");
        put("MARECHAL", "Marechal");
        put("URBI", "Urbi");
        put("SÃO JOSÉ", "São José");
        put("PIONEIRA", "Pioneira");
        put("TCB", "TCB");
    }};

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            jsonGenerator.writeNull();
        } else {
            String newVal = operadoresMap.entrySet().stream()
                    .filter(entry -> value.toUpperCase().contains(entry.getKey()))
                    .findFirst()
                    .map(entry -> entry.getValue())
                    .orElse(value);

            jsonGenerator.writeString(newVal);
        }
    }

}
