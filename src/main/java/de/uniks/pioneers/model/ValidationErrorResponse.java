package de.uniks.pioneers.model;

import java.util.List;

public record ValidationErrorResponse(
        int statusCode,
        String error,
        List<String> message
) {
}
