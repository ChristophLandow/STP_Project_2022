package de.uniks.pioneers.model;

public record Move(
        String _id,
        String createdAt,
        String gameId,
        String userId,
        String action,
        int roll,
        String building
) {
}
