package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.Resources;

public record CreateMoveDto(
        String action,
        RobDto rob,
        Resources resources,
        String partner,
        CreateBuildingDto building
) {

    public CreateMoveDto(String action, Resources resources, String partner) {
        this(action, null, resources, partner, null);
    }

    public CreateMoveDto(String action, CreateBuildingDto building) {
        this(action, null, null, null, building);
    }

}
