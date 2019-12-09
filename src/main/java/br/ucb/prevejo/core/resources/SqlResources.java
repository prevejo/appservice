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
