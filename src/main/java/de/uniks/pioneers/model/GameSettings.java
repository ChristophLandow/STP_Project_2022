package de.uniks.pioneers.model;

public record GameSettings(
        int mapRadius,
        int victoryPoints,
        String mapTemplate,
        boolean roll7,
        int startingResources
) {
}
