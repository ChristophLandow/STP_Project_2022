package de.uniks.pioneers.model;

public record LoginResult(
        String id,
        String name,
        String status,
        String avatar,
        String accessToken,
        String refreshToken

) {

}
