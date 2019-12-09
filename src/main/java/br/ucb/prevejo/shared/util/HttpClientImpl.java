package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.shared.exceptions.HttpRequestException;
import br.ucb.prevejo.shared.intefaces.HttpClient;
import br.ucb.prevejo.shared.intefaces.Parser;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.measure.format.ParserException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class HttpClientImpl implements HttpClient {

    private HttpClientContext context = HttpClientContext.create();
    private org.apache.http.client.HttpClient httpclient = HttpClients.createDefault();

    @Override
    public <T> T get(Parser<String, T> parser, URI uri) throws HttpRequestException {
        try {
            HttpResponse response = httpclient.execute(new HttpGet(uri));

            return parser.parse(EntityUtils.toString(response.getEntity()));
        } catch(IOException | ParserException e) {
            throw new HttpRequestException(e);
        }
    }

    @Override
    public <T> T get(Parser<String, T> parser, String uri) throws HttpRequestException {
        return get(parser, uri, null);
    }

    @Override
    public <T> T get(Parser<String, T> parser, String uri, Map<String, String> queryParams) throws HttpRequestException {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);

            if (queryParams != null) {
                queryParams.entrySet().forEach(e -> uriBuilder.addParameter(e.getKey(), e.getValue()));
            }

            return get(parser, uriBuilder.build());
        } catch(URISyntaxException e) {
            throw new HttpRequestException(e);
        }
    }

    @Override
    public <T> T post(Parser<String, T> parser, String uri, String data) throws HttpRequestException {
        try {
            HttpPost request = new HttpPost(uri);
            request.setEntity(new StringEntity(data));

            HttpResponse response = httpclient.execute(request);

            return parser.parse(EntityUtils.toString(response.getEntity()));
        } catch(IOException | ParserException e) {
            throw new HttpRequestException(e);
        }
    }

    @Override
    public <T> T request(Parser<String, T> parser, String scheme, String method, String host, int port, String path) throws HttpRequestException {
        HttpRequest request = getRequest(method, path);
        HttpHost httpHost = getHost(host, port, scheme);

        try {
            HttpResponse response = httpclient.execute(httpHost, request, context);

            return parser.parse(EntityUtils.toString(response.getEntity()));
        } catch(IOException | ParserException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpRequest getRequest(String method, String path) {
        return new BasicHttpRequest(method, path);
    }

    private HttpHost getHost(String host, int port, String scheme) {
        return new HttpHost(host, port, scheme);
    }

}
