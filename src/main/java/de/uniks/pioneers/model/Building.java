package de.uniks.pioneers.model;

public record Building(
        int x,
        int y,
        int z,
        String _id,
        int side,
        String type,
        String gameId,
        String owner
) {
}
