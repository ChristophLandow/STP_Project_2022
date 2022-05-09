package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import java.io.IOException;

public class LoginService {
    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;
    private final UserService userService;

    @Inject
    public LoginService(AuthApiService authApiService, TokenStorage tokenStorage, UserService userService) {
        this.authApiService = authApiService;
        this.tokenStorage = tokenStorage;
        this.userService = userService;
    }

    public Observable<LoginResult> login(String userName, String password) {
        return authApiService.login(new LoginDto(userName, password))
                .doOnNext(result -> {
                    tokenStorage.setAccessToken(result.accessToken());
                    tokenStorage.setRefreshToken(result.refreshToken());
                    userService.setCurrentUserId(result._id());
                });
    }

    public LoginResult checkPassword(String username, String password) {
        LoginResult body;
        try {
            body = authApiService.checkPassword(new LoginDto(username, password)).execute().body();
        } catch (IOException e) {
            return null;
        }
        return body;
    }
}