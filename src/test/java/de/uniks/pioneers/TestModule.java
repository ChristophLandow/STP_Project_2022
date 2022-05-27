package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.TokenStorage;
import de.uniks.pioneers.ws.ClientEndpoint;
import de.uniks.pioneers.ws.EventListener;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import javax.inject.Singleton;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.*;

@Module
public class TestModule {

    @Provides
    @Singleton
    static AuthApiService authApiService(){

        return new AuthApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto dto) {

                return Observable.just(new LoginResult("000", dto.name(), "online", null, "accessToken", "refreshToken"));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto dto) {

                return Observable.just(new LoginResult("000", "TestUser", "online", null, "accessToken", "refreshToken"));
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
    @Singleton
    Preferences prefs(){

        return Preferences.userNodeForPackage(Main.class);
    }
    @Provides
    @Singleton
    static Retrofit retrofit (OkHttpClient client, ObjectMapper mapper){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL+API_PREFIX+"/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .build();
    }
    @Provides
    @Singleton
    static UserApiService userApiService(){
        return new UserApiService() {
            @Override
            public Observable<User> create(CreateUserDto dto) {
                return Observable.just(new User("000",dto.name(),"online",dto.avatar()));
            }

            @Override
            public Observable<User> getUser(String id) {

                return Observable.just(new User(id,"TestUser_" + id,"online",null));
            }

            @Override
            public Observable<User> update(String id, UpdateUserDto dto) {

                return Observable.just(new User(id,"TestUser",dto.status(),dto.avatar()));
            }

            @Override
            public Call<List<User>> getOnlineUsers() {
                return null;
            }

            @Override
            public Observable<List<User>> findAll() {

                ArrayList<User> users = new ArrayList<>();
                users.add(new User("000","TestUserA","online",null));
                users.add(new User("000","TestUserB","online",null));
                users.add(new User("000","TestUserC","online",null));
                return Observable.just(users);
            }
        };
    }
    @Provides
    @Singleton
    static GameApiService gameApiService() {

        return new GameApiService() {
            @Override
            public Observable<List<Game>> getGames() {

                ArrayList<Game> games = new ArrayList<>();
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","TestGameA","TestUserA",1,false));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","002","TestGameB","TestUserB",1,false));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","003","TestGameC","TestUserC",1,false));
                return Observable.just(games);
            }

            @Override
            public Observable<Game> getGame(String id) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestGameA","TestUserA",1,false));
            }

            @Override
            public Observable<Game> create(CreateGameDto dto) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000",dto.name(),"TestUser",1,false));

            }

            @Override
            public Observable<Game> update(String id, UpdateGameDto dto) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","TestUser",1,false));

            }

            @Override
            public Observable<Game> delete(String id) {

                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","TestUser",1,false));

            }
        };

    }

    @Provides
    @Singleton
    static MessageApiService messageApiService() {

        return new MessageApiService() {
            @Override
            public Observable<MessageDto> sendMessage(String namespace, String parent, CreateMessageDto dto) {

                return Observable.just(new MessageDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","A",dto.body()));
            }

            @Override
            public Observable<List<MessageDto>> getChatMessages(String namespace, String parent) {

                ArrayList<MessageDto> messages = new ArrayList<>();
                messages.add(new MessageDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","A","Hallo"));
                messages.add(new MessageDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","B","Hallo2"));
                messages.add(new MessageDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","002","C","Hallo3"));
                return Observable.just(messages);
            }

            @Override
            public Observable<MessageDto> updateMessage(String namespace, String parent, String id, UpdateMessageDto dto) {

                return Observable.just(new MessageDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","A",dto.body()));
            }
        };
    }

    @Provides
    @Singleton
    static GroupApiService groupApiService() {
        return new GroupApiService() {
            @Override
            public Observable<GroupDto> newGroup(CreateGroupDto dto) {

                return Observable.just(new GroupDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000",new ArrayList<>(),"TestGroup"));
            }

            @Override
            public Observable<List<GroupDto>> getGroupsWithUsers(String users) {

                ArrayList<GroupDto> groups = new ArrayList<>();
                groups.add(new GroupDto("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000",new ArrayList<>(),"TestGroup"));
                return Observable.just(groups);
            }
        };
    }

    @Provides
    @Singleton
    static GameMemberApiService gameMemberApiService() {

        return new GameMemberApiService() {
            @Override
            public Observable<List<Member>> getAll(String gameId) {

                ArrayList<Member> users = new ArrayList<>();
                users.add(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",true));
                users.add(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"001",false));
                return Observable.just(users);
            }

            @Override
            public Observable<Member> createMember(String gameId, CreateMemberDto dto) {

                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",false));
            }

            @Override
            public Observable<Member> deleteMember(String gameId, String userId) {

                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",false));
            }

            @Override
            public Observable<Member> setReady(String gameId, String userId, UpdateMemberDto dto) {

                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",true));
            }
        };
    }

    @Provides
    static NewGameLobbyService newGameLobbyService(){
        return new NewGameLobbyService(gameApiService(),gameMemberApiService(),messageApiService()){

            private String currentMemberId;

            public Observable<List<Member>> getAll(String id){
                return gameMemberApiService().getAll(id);
            }

            public Observable<Member> postMember(String id, boolean ready, String password){
                return gameMemberApiService().createMember(id, new CreateMemberDto(ready, password));
            }

            public Observable<Member> deleteMember(String id, String userId){
                return gameMemberApiService().deleteMember(id,userId);
            }

            public Observable<MessageDto> sendMessage(String id, CreateMessageDto dto) {
                return messageApiService().sendMessage("games", id, dto);
            }

            public Observable<List<MessageDto>> getMessages(String id){
                return messageApiService().getChatMessages("games", id);
            }

            public Observable<Member> setReady(String groupId, String userId) {
                return gameMemberApiService().setReady(groupId, userId, new UpdateMemberDto(true));
            }

            public void setCurrentMemberId(String id) {
                currentMemberId = id;
            }

            public String getCurrentMemberId() {
                return currentMemberId;
            }
        };
    }



    @Provides
    @Singleton
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

