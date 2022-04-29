package de.uniks.pioneers.services;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class LobbyService {
    private final UserApiService userApiService;

    @Inject
    public LobbyService(UserApiService userApiService){
        this.userApiService = userApiService;
    }

    public List<User> userList(){
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2MjZiYWU3ZmIwZTUyZjAwMTQwMzUzZjMiLCJyZWZyZXNoS2V5Ijoic0xiRUovTGg4UG1WWkdsU0ZXTmtJVWxDSUJiaXVkbDEyaXp1MmdNTUVOeDZsTmc4eXBkWHBoMEd3U3RtQ1cwd3BkOFpZUlAwL3RJMFd4WWRieUlMZlE9PSIsImlhdCI6MTY1MTIyNDIwMiwiZXhwIjoxNjUzNjQzNDAyfQ.IhmwqTTAKft66bE-AoIE_EjIoPZUh3pKTM-d-ZNtUwM";

        try{
            return userApiService.getOnlineUsers("Bearer " + accessToken).execute().body();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
