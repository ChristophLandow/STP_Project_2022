package de.uniks.pioneers.model;

import de.uniks.pioneers.dto.RobDto;

public record Move(
        String createdAt,
        String _id,
        String gameId,
        String userId,
        String action,
        int roll,
        String building,
        RobDto rob,
        Resources resources,
        String partner,
        String developmentCard
) {
}
