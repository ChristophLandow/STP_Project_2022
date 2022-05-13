package de.uniks.pioneers.model;

public record Member(

    String createdAt,
    String updatedAt,
    String gameId,
    String UserId,
    boolean ready
) {}
