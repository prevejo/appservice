package br.ucb.prevejo.core.resources;

import br.ucb.prevejo.core.ContextProvider;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlResources {

    private static final String PARENT_PATH = "sql";
    public static final String PARADAS_BY_DISTANCIA = "paradasByDistancia.sql";
    public static final String PARADAS_IN_RANGE = "paradasInRange.sql";
    public static final String INSERT_INSTANTE_OPERACAO = "insertInstanteOperacao.sql";
    public static final String SELECT_INSTANTE_OPERACAO_BY_LINHA = "selectInstanteOperacaoByLinha.sql";
    public static final String SELECT_INSTANTE_OPERACAO_BY_LINHA_IN_RANGE = "selectInstanteOperacaoByLinhaInRange.sql";
    public static final String SELECT_INSTANTE_OPERACAO_BY_LINHA_AND_SENTIDO = "selectInstanteOperacaoByLinhaAndSentido.sql";
    public static final String SELECT_INSTANTE_OPERACAO_BY_LINHA_AND_SENTIDO_IN_RANGE = "selectInstanteOperacaoByLinhaAndSentidoInRange.sql";
    private static Set<String> envs;

    public static String find(String sqlName) {
        try {
            return Resources.asString(PARENT_PATH + "/" + getEnvironmentPath() + "/" + sqlName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getEnvironmentPath() {
        if (envs == null) {
            envs = Arrays.asList(ContextProvider.getBean(Environment.class).getActiveProfiles()).stream().collect(Collectors.toSet());
        }

        if (envs.contains("prod") || envs.contains("dev")) {
            return "prod";
        }

        return "devmem";
    }

}
