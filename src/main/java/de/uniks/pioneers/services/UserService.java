package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import javafx.scene.control.Alert;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public Response<User> register(String name, String password) {
        try {
            Response<User> response = userApiService.create(new CreateUserDto(name, password)).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


