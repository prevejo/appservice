package br.ucb.prevejo.shared.intefaces;

import br.ucb.prevejo.shared.exceptions.HttpRequestException;

import java.net.URI;
import java.util.Map;

public interface HttpClient {

    public <T> T get(Parser<String, T> parser, URI uri) throws HttpRequestException;
    public <T> T get(Parser<String, T> parser, String uri) throws HttpRequestException;
    public <T> T get(Parser<String, T> parser, String uri, Map<String, String> queryParams) throws HttpRequestException;
    public <T> T post(Parser<String, T> parser, String uri, String data) throws HttpRequestException;
    public <T> T request(Parser<String, T> parser, String scheme, String method, String host, int port, String path) throws HttpRequestException;

}
