package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.Resources;
import static de.uniks.pioneers.GameConstants.BUILD;

public record CreateMoveDto(
        String action,
        RobDto rob,
        Resources resources,
        String partner,
        String developmentCard,
        CreateBuildingDto building
) {

    public CreateMoveDto(String action, Resources resources, String partner) {
        this(action, null, resources, partner, null, null);
    }

    public CreateMoveDto(String action, CreateBuildingDto building) {
        this(action, null, null, null, null, building);
    }

    public CreateMoveDto(String action, Resources resources) {
        this(action, null, resources, null, null, null);
    }

    public CreateMoveDto(String action, String partner) {
        this(action, null, null, partner, null, null);
    }

    public CreateMoveDto(String action) {
        this(action, null, null, null, null, null);
    }

    public CreateMoveDto() {
        this(BUILD, null, null, null, "new", null);
    }

    public CreateMoveDto(String devCard, boolean yourAdvertisementCouldBeHere) {
        this(BUILD, null, null, null, devCard, null);
    }
}
