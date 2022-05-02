package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.LogoutResult;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<LoginResult> login(@Body LoginDto dto);

    @POST("auth/logout")
    Observable<LogoutResult> logout();
}
