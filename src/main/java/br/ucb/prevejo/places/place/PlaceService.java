package br.ucb.prevejo.places.place;

import br.ucb.prevejo.shared.intefaces.GooglePlacesService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlaceService {

    private static final Map<String, String> DETAILS_PARAMS = new HashMap<String, String>() {{
        put("fields", "place_id,name,geometry");
        put("language", "pt-BR");
    }};

    private GooglePlacesService service;

    public PlaceService(GooglePlacesService service) {
        this.service = service;
    }

    public Place detail(String placeId, String searchSession) {
        return service.detailPlace(placeId, searchSession, jn -> Place.build(jn), DETAILS_PARAMS);
    }

}
