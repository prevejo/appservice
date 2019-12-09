package br.ucb.prevejo.core.resources;

import br.ucb.prevejo.core.config.WebServiceConfig;

import java.io.IOException;
import java.util.Properties;

public class WebServiceResources {

    private static final String RESOURCE_FILE = "web-services.properties";

    public static WebServiceConfig findConfig(String id) {
        Properties props = new Properties();

        try {
            props.load(Resources.asInputStream(RESOURCE_FILE));

            return new WebServiceConfig(
                    id,
                    props.getProperty(id + ".host"),
                    Integer.valueOf(props.getProperty(id + ".port")),
                    props.getProperty(id + ".hostheader"),
                    props.getProperty(id + ".path"),
                    "true".equalsIgnoreCase(props.getProperty(id + ".closeConnectionMode"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
