package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.TileTemplate;

public record UpdateMapTemplateDto(
        String name,
        String icon,
        TileTemplate tiles,
        HarborTemplate harbors
) {
}
