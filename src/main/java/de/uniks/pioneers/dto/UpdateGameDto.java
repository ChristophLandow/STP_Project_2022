package de.uniks.pioneers.dto;

public record UpdateGameDto(
        String name,
        String owner,
        Boolean started,
        String password
) {}
