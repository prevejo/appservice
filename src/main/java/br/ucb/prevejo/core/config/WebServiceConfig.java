package br.ucb.prevejo.core.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebServiceConfig {

    private String id;
    private String host;
    private Integer port;
    private String hostHeader;
    private String path;
    private Boolean closeConnectionMode;

}
