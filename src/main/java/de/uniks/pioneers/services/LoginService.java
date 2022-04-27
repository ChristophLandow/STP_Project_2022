package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.rest.AuthApiService;

import javax.inject.Inject;

public class LoginService {

    private final AuthApiService authApiService;

    @Inject
    public LoginService(AuthApiService authApiService  ) {
        this.authApiService = authApiService;
    }

    public void login(String name, String password){
        authApiService.login(new LoginDto(name,password));
    }
}
