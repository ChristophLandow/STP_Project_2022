package de.uniks.pioneers.model;

public record MapTemplate(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String icon,
        String createdBy,
        int votes,
        TileTemplate tiles,
        HarborTemplate harbors
) {
}
