package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;


import javax.inject.Inject;


public class LoginService {
    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;

    @Inject
    public LoginService(AuthApiService authApiService, TokenStorage tokenStorage){

        this.authApiService = authApiService;
        this.tokenStorage = tokenStorage;
    }
    public Observable<LoginResult> login(String userName, String password) {


        return authApiService.login(new LoginDto(userName, password))
                .doOnNext(result -> tokenStorage.setToken(result.accessToken()));
    }
}