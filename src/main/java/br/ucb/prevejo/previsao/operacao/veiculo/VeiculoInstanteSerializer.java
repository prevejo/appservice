package br.ucb.prevejo.previsao.operacao.veiculo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VeiculoInstanteSerializer extends StdSerializer<VeiculoInstante> {

    public VeiculoInstanteSerializer() {
        super(VeiculoInstante.class);
    }

    @Override
    public void serialize(VeiculoInstante veiculoInstante, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("veiculo", veiculoInstante.getInstante());
        jsonGenerator.writeObjectField("historico", veiculoInstante.getHistorico());

        jsonGenerator.writeEndObject();
    }

}
