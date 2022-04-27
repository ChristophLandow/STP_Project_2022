package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;

import javax.inject.Inject;
import java.io.IOException;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void register(String name, String password){
        try {
            User body = userApiService.create(new CreateUserDto(name, password)).execute().body();
            System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
