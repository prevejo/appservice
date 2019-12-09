package br.ucb.prevejo.core.resources;

import br.ucb.prevejo.core.ContextProvider;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Resources {

    public static String asString(String resourcePath) throws IOException {
        InputStream in = asInputStream(resourcePath);

        try {
            return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } finally {
            in.close();
        }
    }

    public static InputStream asInputStream(String resourcePath) throws IOException {
        Resource resource = ContextProvider.getResource("classpath:" + resourcePath);

        return resource.getInputStream();
    }

}
