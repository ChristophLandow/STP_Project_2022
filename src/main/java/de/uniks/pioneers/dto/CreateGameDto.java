package de.uniks.pioneers.dto;

public record CreateGameDto(
        String name,
        boolean started,
        String password
) {}
