package de.uniks.pioneers.model;

public record Building(
        double x,
        double y,
        double z,
        String _id,
        int side,
        String type,
        String gameId,
        String owner
) {
}
