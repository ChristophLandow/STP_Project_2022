package de.uniks.pioneers.services;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {
    private String accessToken;
    private String refreshToken;

    @Inject
    public TokenStorage() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
