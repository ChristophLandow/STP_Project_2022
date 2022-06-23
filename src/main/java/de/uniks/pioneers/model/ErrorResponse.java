package de.uniks.pioneers.model;

public record ErrorResponse(
        int statusCode,
        String error,
        String message
) {
}
