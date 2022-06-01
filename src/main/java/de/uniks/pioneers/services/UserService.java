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
    private User currentUser;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }
    public Observable<User> register(String userName,String avatar, String password) {
        return userApiService.create(new CreateUserDto(userName, avatar, password));
    }
    public Observable<User> editProfile(String name, String avatar, String password, String status) {
        return userApiService.update(this.currentUser._id(), new UpdateUserDto(name, avatar, password, status))
                .doOnNext(this::setCurrentUser);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Observable<User> getUserById(String id) {
        return this.userApiService.getUser(id);
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public Observable<List<User>> findAll() {
        return this.userApiService.findAll();
    }
}


