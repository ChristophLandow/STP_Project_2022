package de.uniks.pioneers.model;

import java.util.List;

public record MapTemplate(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String icon,
        String createdBy,
        int votes,
        List<TileTemplate> tiles,
        List<HarborTemplate> harbors
) {
}
