package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.RefreshDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.TokenStorage;
import de.uniks.pioneers.ws.EventListener;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.API_V1_PREFIX;
import static de.uniks.pioneers.Constants.BASE_URL;

@Module
public class TestModule {

    //Provisionally uses Server functionality! Adjustments for respective Tests required!

    @Provides
    static EventListener eventListener(){

        return new EventListener(null,null){

            @Override
            public <T> Observable<Event<T>> listen(String pattern, Class<T> type) {

                return Observable.empty();
            }

            private void send(Object message){}
        };
    }

    @Provides
    static AuthApiService authApiService(){

        return new AuthApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto dto) {
                return null;
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto dto) {
                return null;
            }

            @Override
            public Call<LoginResult> checkPassword(LoginDto dto) {
                return null;
            }

            @Override
            public Observable<LogoutResult> logout() {
                return null;
            }
        };
    }

    @Provides
    @Singleton
    static ObjectMapper mapper(){
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    static OkHttpClient client(TokenStorage tokenStorage) {
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            final String token = tokenStorage.getAccessToken();
            if (token == null) {
                return chain.proceed(chain.request());
            }
            final Request newRequest = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }).build();
    }
    @Provides
    Preferences prefs(){

        return Preferences.userNodeForPackage(Main.class);
    }
    @Provides
    @Singleton
    static Retrofit retrofit (OkHttpClient client, ObjectMapper mapper){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL+API_V1_PREFIX+"/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .build();
    }
    @Provides
    static UserApiService userApiService(Retrofit retrofit){
        return retrofit.create(UserApiService.class);
    }
    @Provides
    static GameApiService gameApiService(Retrofit retrofit) { return retrofit.create(GameApiService.class); }

    @Provides
    static MessageApiService messageApiService(Retrofit retrofit) { return retrofit.create(MessageApiService.class); }

    @Provides
    static GroupApiService groupApiService(Retrofit retrofit) { return retrofit.create(GroupApiService.class); }

    @Provides
    static GameMemberApiService gameMemberApiService(Retrofit retrofit) { return retrofit.create(GameMemberApiService.class); }

    @Provides
    static PrefService prefService() {

        return new PrefService(null, null,null){

            @Override
            public String recall(){

                return "";
            }
        };}


}

