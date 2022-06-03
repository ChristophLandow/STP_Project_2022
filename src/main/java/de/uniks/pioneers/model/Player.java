package de.uniks.pioneers.model;

import java.util.List;

public record Player(
        String gameId,
        String userId,
        String color,
        int foundingRoll
//        List<Integer> resources,
//        List<Integer> remainingBuildings
) {
}
