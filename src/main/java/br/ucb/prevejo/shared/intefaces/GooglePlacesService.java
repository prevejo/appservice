package br.ucb.prevejo.shared.intefaces;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface GooglePlacesService {

    public <T> List<T> searchPlaces(String value, String session, Parser<JsonNode, T> parser, Map<String, String> optionalParams);

    public <T> T detailPlace(String placeId, String session, Parser<JsonNode, T> parser, Map<String, String> optionalParams);

}
