package de.uniks.pioneers.dto;

public record MessageDto(
        String createdAt,
        String updatedAt,
        String _id,
        String sender,
        String body
) {
}
