package br.ucb.prevejo.places.search;

import br.ucb.prevejo.shared.intefaces.GooglePlacesService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlaceSearchService {

    private static final Map<String, String> SEARCH_PARAMS = new HashMap<String, String>() {{
        put("location", "-15.75159,-47.79583");
        put("radius", "57000");
        put("language", "pt-BR");
    }};

    private GooglePlacesService client;

    private PlaceSearchService(GooglePlacesService client) {
        this.client = client;
    }

    public Collection<PlaceSearchResult> search(String value, String searchSession) {
        return client.searchPlaces(value, searchSession, jn -> PlaceSearchResult.build(jn), SEARCH_PARAMS);
    }

}
