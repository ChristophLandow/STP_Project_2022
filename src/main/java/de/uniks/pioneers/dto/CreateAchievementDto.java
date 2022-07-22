package de.uniks.pioneers.dto;

public record CreateAchievementDto(
        String unlockedAt,
        int progress
) {
}
