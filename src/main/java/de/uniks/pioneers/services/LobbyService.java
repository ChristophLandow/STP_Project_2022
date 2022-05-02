package de.uniks.pioneers.services;

import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class LobbyService {
    private final UserApiService userApiService;
    private final AuthApiService authApiService;

    @Inject
    public LobbyService(UserApiService userApiService, AuthApiService authApiService){
        this.userApiService = userApiService;
        this.authApiService = authApiService;
    }

    public List<User> userList(){
        try{
            return userApiService.getOnlineUsers().execute().body();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public Observable<LogoutResult> logout() {
        return authApiService.logout();
    }
}
