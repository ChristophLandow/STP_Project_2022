package de.uniks.pioneers.model;

public record Achievement(
        String createdAt,
        String updatedAt,
        String userId,
        String id,
        String unlockedAt,
        int progress
) {
}
