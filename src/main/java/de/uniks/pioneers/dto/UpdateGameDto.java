package de.uniks.pioneers.dto;

public record UpdateGameDto(
        String name,
        String owner,
        boolean started,
        String password
) {}
