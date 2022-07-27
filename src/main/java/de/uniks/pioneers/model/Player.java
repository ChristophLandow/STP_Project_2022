package de.uniks.pioneers.model;

import java.util.List;

public record Player(
        String gameId,
        String userId,
        String color,
        boolean active,
        Integer foundingRoll,
        Resources resources,
        RemainingBuildings remainingBuildings,
        int victoryPoints,
        int longestRoad,
        List<DevelopmentCard> developmentCards
) {

    public Player normalize(Resources normalizedResources) {
        return new Player(gameId, userId, color, active, foundingRoll, normalizedResources, remainingBuildings, victoryPoints, longestRoad, developmentCards);
    }
}


