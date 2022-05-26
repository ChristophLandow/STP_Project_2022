package de.uniks.pioneers.dto;

public record CreateGameDto(
        String name,
        Boolean started,
        String password
) {}
