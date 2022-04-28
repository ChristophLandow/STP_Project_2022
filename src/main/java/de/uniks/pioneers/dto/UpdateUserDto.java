package de.uniks.pioneers.dto;

public record UpdateUserDto(
        String name,
        String avatar,
        String password
) {
}
