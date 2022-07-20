package de.uniks.pioneers.model;

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
        DevelopmentCard developmentCards
) {

    public Player normalize(Resources normalizedResources) {
        return new Player(gameId, userId, color, active, foundingRoll, normalizedResources, remainingBuildings, victoryPoints, longestRoad, developmentCards);
    }
}


