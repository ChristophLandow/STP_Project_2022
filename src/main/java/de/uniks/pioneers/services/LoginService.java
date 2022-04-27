package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.function.Consumer;

public class LoginService {

    private final AuthApiService authApiService;

    @Inject
    public LoginService(AuthApiService authApiService  ) {
        this.authApiService = authApiService;
    }

    public void login(String name, String password, Consumer<? super LoginResult> callback){
        authApiService.login(new LoginDto(name,password)).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

        };
    }
}
