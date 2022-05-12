package de.uniks.pioneers.dto;

import java.util.List;

public record GroupDto(
        String createdAt,
        String updatedAt,
        String _id,
        List<String> members,
        String name
) {
}