package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface UserApiService {
    @POST("users")
    Call<User> create(@Body CreateUserDto dto);
}
