package de.uniks.pioneers.model;

import java.util.List;

public record Player(String gameId,
                     String userId,
                     String color,
                     Integer foundingRoll,
                     Resources resources,
                     RemainingBuildings remainingBuildings
                     ) {}


