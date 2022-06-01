package de.uniks.pioneers.dto;

public record CreateBuildingDto(
        int x,
        int y,
        int z,
        int side,
        String type
) {
}
