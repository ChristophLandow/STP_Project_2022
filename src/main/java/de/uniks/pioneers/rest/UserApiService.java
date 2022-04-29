package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Body;
import java.util.List;

public interface UserApiService {
    @POST("users")
    Call<User> create(@Body CreateUserDto dto);

    @PATCH("users")
    Call<User> update(@Body UpdateUserDto dto);
    
    @GET("users/?status=online")
    Call<List<User>> getOnlineUsers(@Header("Authorization") String accessToken);
}
