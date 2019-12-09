package br.ucb.prevejo.places.place;

import br.ucb.prevejo.shared.intefaces.JsonNodeEntity;
import br.ucb.prevejo.shared.model.Location;
import br.ucb.prevejo.shared.model.LocationViewPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
public class Place implements JsonNodeEntity {

    private String id;
    private String name;
    private Location location;
    private LocationViewPort viewPort;

    @Override
    public void setFromJsonNode(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("result")) {
            jsonNode = jsonNode.get("result");

            if (jsonNode.hasNonNull("place_id")) {
                setId(jsonNode.get("place_id").asText());
            }
            if (jsonNode.hasNonNull("name")) {
                setName(jsonNode.get("name").asText());
            }

            if (jsonNode.hasNonNull("geometry")) {
                JsonNode geometry = jsonNode.get("geometry");

                Function<JsonNode, Location> toLocation = jn -> Location.build(jn.get("lat").asText(), jn.get("lng").asText());

                if (geometry.hasNonNull("location")) {
                    setLocation(toLocation.apply(geometry.get("location")));
                }

                if (geometry.hasNonNull("viewport")) {
                    if (geometry.get("viewport").hasNonNull("northeast") && geometry.get("viewport").hasNonNull("southwest")) {
                        setViewPort(LocationViewPort.build(
                                toLocation.apply(geometry.get("viewport").get("northeast")),
                                toLocation.apply(geometry.get("viewport").get("southwest"))
                        ));
                    }
                }
            }
        }
    }

    public static Place build(JsonNode jsonNode) {
        Place place = new Place();
        place.setFromJsonNode(jsonNode);
        return place;
    }

}
