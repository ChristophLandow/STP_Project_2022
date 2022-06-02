package de.uniks.pioneers.dto;

public record CreateBuildingDto(
        double x,
        double y,
        double z,
        int side,
        String type
) {
}
