package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import javafx.scene.control.Alert;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.function.Consumer;

public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void register(String name, String password, Consumer<Response<User>> responseComsumer) {
        userApiService.create(new CreateUserDto(name, password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                responseComsumer.accept(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}


