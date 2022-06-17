package de.uniks.pioneers.model;

public record Player(
        String gameId,
        String userId,
        String color,
        Integer foundingRoll,
        Resources resources,
        RemainingBuildings remainingBuildings
) {
}


