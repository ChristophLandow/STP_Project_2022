package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.API_V1_PREFIX;
import static de.uniks.pioneers.Constants.BASE_URL;

@Module
public class TestModule {

    @Provides
    static EventListener eventListener(){

        return new EventListener(null,null){

            @Override
            public <T> Observable<Event<T>> listen(String pattern, Class<T> type) {

                if(pattern == "users.*.*" & type == User.class){

                }
                if(pattern == "\"games.*.*\"" & type == Game.class){

                }

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

                return Observable.just(new LoginResult("000", "TestUser", "online", null, "accesToken", "refreshToken"));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto dto) {

                return Observable.just(new LoginResult("000", "TestUser", "online", null, "accesToken", "refreshToken"));
            }

            @Override
            public Call<LoginResult> checkPassword(LoginDto dto) {
                return null;
            }

            @Override
            public Observable<LogoutResult> logout() {

                return Observable.just(new LogoutResult());
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
    static UserApiService userApiService(){
        return new UserApiService() {
            @Override
            public Observable<User> create(CreateUserDto dto) {
                return Observable.just(new User("000","TestUser","online",null));
            }

            @Override
            public Observable<User> getUser(String id) {

                return Observable.just(new User("000","TestUser","online",null));
            }

            @Override
            public Observable<User> update(String id, UpdateUserDto dto) {

                return Observable.just(new User("000","TestUser","online",null));
            }

            @Override
            public Call<List<User>> getOnlineUsers() {
                return null;
            }

            @Override
            public Observable<List<User>> findAll() {

                ArrayList users = new ArrayList<>();
                users.add(new User("000","TestUserA","online",null));
                users.add(new User("000","TestUserB","online",null));
                users.add(new User("000","TestUserC","online",null));
                return Observable.just(users);
            }
        };
    }
    @Provides
    static GameApiService gameApiService() {

        return new GameApiService() {
            @Override
            public Observable<List<Game>> getGames() {

                ArrayList games = new ArrayList<>();
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","TestGameA","TestUserA",1));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","002","TestGameB","TestUserB",1));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","003","TestGameC","TestUserC",1));
                return Observable.just(games);
            }

            @Override
            public Observable<Game> getGame(String id) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestGameA","TestUserA",1));
            }

            @Override
            public Observable<Game> create(CreateGameDto dto) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","TestUser",1));

            }

            @Override
            public Observable<Game> update(String id, UpdateGameDto dto) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","TestUser",1));

            }

            @Override
            public Observable<Game> delete(String id) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","TestUser",1));

            }
        };

    }

    @Provides
    static MessageApiService messageApiService(Retrofit retrofit) { return retrofit.create(MessageApiService.class); }

    @Provides
    static GroupApiService groupApiService() {
        return new GroupApiService() {
            @Override
            public Observable<GroupDto> newGroup(CreateGroupDto dto) {
                return null;
            }

            @Override
            public Observable<List<GroupDto>> getGroupsWithUsers(String users) {
                return null;
            }
        };
    }

    @Provides
    static GameMemberApiService gameMemberApiService() {

        return new GameMemberApiService() {
            @Override
            public Observable<List<Member>> getAll(String gameId) {
                return null;
            }

            @Override
            public Observable<Member> createMember(String gameId, CreateMemberDto dto) {
                return null;
            }

            @Override
            public Observable<Member> deleteMember(String gameId, String userId) {
                return null;
            }
        };
    }

    @Provides
    static PrefService prefService() {

        return new PrefService(null, null,null){

            @Override
            public String recall(){

                return "";
            }
            @Override
            public void remember(){}
        };}


}

