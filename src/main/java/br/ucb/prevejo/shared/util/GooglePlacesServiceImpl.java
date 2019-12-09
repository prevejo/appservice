package br.ucb.prevejo.shared.util;

import br.ucb.prevejo.shared.exceptions.HttpRequestException;
import br.ucb.prevejo.shared.exceptions.ParseException;
import br.ucb.prevejo.shared.exceptions.ServiceException;
import br.ucb.prevejo.shared.intefaces.GooglePlacesService;
import br.ucb.prevejo.shared.intefaces.HttpClient;
import br.ucb.prevejo.shared.intefaces.Parser;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GooglePlacesServiceImpl implements GooglePlacesService {

    private HttpClient client;

    @Value("${api.places.url}")
    private String api;

    @Value("${api.places.search}")
    private String searchPath;

    @Value("${api.places.detail}")
    private String detailPath;

    @Value("${api.places.key}")
    private String key;

    public GooglePlacesServiceImpl(HttpClient client) {
        this.client = client;
    }

    public <T> List<T> searchPlaces(String value, String session, Parser<JsonNode, T> parser, Map<String, String> optionalParams) {
        Map<String, String> params = new HashMap<String, String>() {{
            optionalParams.forEach((k, v) -> put(k, v));
            put("key", key);
            put("input", value);
            put("sessiontoken", session);
        }};

        String uri = api + searchPath + "/json";

        try {
            JsonNode node = client.get(new JsonNodeDeserializer(), uri, params);

            return StreamSupport.stream(node.get("predictions").spliterator(), false)
                    .map(jn -> parser.parse(jn))
                    .collect(Collectors.toList());
        } catch(HttpRequestException | ParseException e) {
            throw new ServiceException(e);
        }
    }

    public <T> T detailPlace(String placeId, String session, Parser<JsonNode, T> parser, Map<String, String> optionalParams) {
        Map<String, String> params = new HashMap<String, String>() {{
            optionalParams.forEach((k, v) -> put(k, v));
            put("key", key);
            put("sessiontoken", session);
            put("placeid", placeId);
        }};

        String uri = api + detailPath + "/json";

        try {
            JsonNode node = client.get(new JsonNodeDeserializer(), uri, params);

            return parser.parse(node);
        } catch(HttpRequestException | ParseException e) {
            throw new ServiceException(e);
        }
    }

}
