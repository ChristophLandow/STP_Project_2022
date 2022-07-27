package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserApiService {
    @POST("users")
    Observable<User> create(@Body CreateUserDto dto);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> update(@Path("id") String id, @Body UpdateUserDto dto);

    @GET("users/?status=online")
    Call<List<User>> getOnlineUsers();

    @GET("users/?status=online")
    Observable<List<User>> findAll();
}
