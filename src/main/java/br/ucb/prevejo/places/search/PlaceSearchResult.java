package br.ucb.prevejo.places.search;

import br.ucb.prevejo.shared.intefaces.JsonNodeEntity;
import br.ucb.prevejo.shared.model.EntityTitle;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class PlaceSearchResult implements JsonNodeEntity {

    private String id;
    private EntityTitle title;

    @Override
    public void setFromJsonNode(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("place_id")) {
            this.setId(jsonNode.get("place_id").asText());
        }

        if (jsonNode.hasNonNull("structured_formatting") && jsonNode.hasNonNull("description")) {
            if (jsonNode.get("structured_formatting").hasNonNull("main_text") && jsonNode.get("structured_formatting").hasNonNull("secondary_text")) {
                this.setTitle(EntityTitle.build(
                        jsonNode.get("structured_formatting").get("main_text").asText(),
                        jsonNode.get("structured_formatting").get("secondary_text").asText(),
                        jsonNode.get("description").asText()
                ));
            }
        }
    }

    public static PlaceSearchResult build(JsonNode jsonNode) {
        PlaceSearchResult result = new PlaceSearchResult();
        result.setFromJsonNode(jsonNode);
        return result;
    }

}
