package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;

public class UserService {

    private final UserApiService userApiService;
    private static User currentUser;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public Observable<User> register(String userName, String password) {
        return userApiService.create(new CreateUserDto(userName, password));
    }

    public Observable<User> editProfile(String name, String avatar, String password) {
        currentUser = new User(currentUser._id(), name, currentUser.status(), avatar);
        return userApiService.update(currentUser._id(), new UpdateUserDto(name, avatar, password));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}


