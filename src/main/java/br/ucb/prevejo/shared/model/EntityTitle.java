package br.ucb.prevejo.shared.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityTitle {

    private String description;
    private String mainTitle;
    private String secondaryTitle;

    private EntityTitle(String mainTitle, String secondaryTitle, String description) {
        setDescription(description);
        setMainTitle(mainTitle);
        setSecondaryTitle(secondaryTitle);
    }

    public static EntityTitle build(String mainTitle, String secondaryTitle, String description) {
        return new EntityTitle(mainTitle, secondaryTitle, description);
    }

}
