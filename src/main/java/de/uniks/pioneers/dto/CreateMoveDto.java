package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.Building;

public record CreateMoveDto(
        String action,
        CreateBuildingDto building
) {
}
