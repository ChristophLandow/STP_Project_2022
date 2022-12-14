package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.services.TokenStorage;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.API_PREFIX;
import static de.uniks.pioneers.Constants.BASE_URL;

@Module
public class MainModule {
    @Provides
    @Singleton
    ObjectMapper mapper(){
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    OkHttpClient client(TokenStorage tokenStorage) {
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
    Preferences prefs() {
        return Preferences.userNodeForPackage(Main.class);
    }

    @Provides
    @Singleton
    Retrofit retrofit (OkHttpClient client, ObjectMapper mapper){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL+API_PREFIX+"/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .build();
    }

    @Provides
    @Singleton
    UserApiService userApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

    @Provides
    @Singleton
    AchievementsApiService achievementsApiService(Retrofit retrofit) {
        return retrofit.create(AchievementsApiService.class);
    }

    @Provides
    @Singleton
    AuthApiService authApiService(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }

    @Provides
    @Singleton
    GameApiService gameApiService(Retrofit retrofit) {
        return retrofit.create(GameApiService.class);
    }

    @Provides
    @Singleton
    MessageApiService messageApiService(Retrofit retrofit) {
        return retrofit.create(MessageApiService.class);
    }

    @Provides
    @Singleton
    GroupApiService groupApiService(Retrofit retrofit) {
        return retrofit.create(GroupApiService.class);
    }

    @Provides
    @Singleton
    GameMemberApiService gameMemberApiService(Retrofit retrofit) {
        return retrofit.create(GameMemberApiService.class);
    }

    @Provides
    @Singleton
    PioneersApiService pioneersApiService(Retrofit retrofit) {
        return  retrofit.create(PioneersApiService.class);
    }

    @Provides
    @Singleton
    MapApiService mapApiService(Retrofit retrofit) {
        return  retrofit.create(MapApiService.class);
    }

    @Provides
    @Singleton
    VoteApiService voteApiService(Retrofit retrofit) { return retrofit.create(VoteApiService.class);}
}
