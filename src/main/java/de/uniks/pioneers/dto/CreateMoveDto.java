package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Resources;

public record CreateMoveDto(
        String action,
        RobDto rob,
        Resources resources,
        String partner,
        CreateBuildingDto building
) {
}
