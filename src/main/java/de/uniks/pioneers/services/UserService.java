package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UserService {

    private final UserApiService userApiService;
    private String currentUserId;

    @Inject
    public UserService(UserApiService userApiService) {

        this.userApiService = userApiService;
    }

    public Observable<User> register(String userName,String avatar, String password) {
        return userApiService.create(new CreateUserDto(userName, avatar, password));
    }

    public Observable<User> editProfile(String name, String avatar, String password) {
        return userApiService.update(this.currentUserId, new UpdateUserDto(name, avatar, password));
    }

    public Observable<User> getCurrentUser() {
        return this.userApiService.getUser(this.currentUserId);
    }

    public void setCurrentUserId(String id) {
        this.currentUserId = id;
    }

    public String getCurrentUserId() {
        return this.currentUserId;
    }

    public Observable<List<User>> findAll() {
        return this.userApiService.findAll();
    }
}


