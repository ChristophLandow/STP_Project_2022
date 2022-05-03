package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Alert;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.function.Consumer;

public class UserService {

    private final UserApiService userApiService;
    private User currentUser;

    @Inject
    public UserService(UserApiService userApiService) {

        this.userApiService = userApiService;
    }
    public Observable<User> register(String userName,String avatar, String password) {

        return userApiService.create(new CreateUserDto(userName, avatar, password));
    }
    public void editProfile(String name, String password, String avatar, Consumer<Response<User>> responseConsumer) {
        userApiService.update(new UpdateUserDto(name, avatar, password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                responseConsumer.accept(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}


