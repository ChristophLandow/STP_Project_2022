package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import static de.uniks.pioneers.Constants.*;
import static org.mockito.Mockito.*;

@Module
public class TestModule {
    public static PublishSubject<Event<Member>> gameMemberSubject = PublishSubject.create();
    public static PublishSubject<Event<Game>> gameSubject = PublishSubject.create();
    public static PublishSubject<Event<State>> gameStateSubject = PublishSubject.create();
    public static PublishSubject<Event<Building>> gameBuildingSubject = PublishSubject.create();
    public static PublishSubject<Event<Move>> gameMoveSubject = PublishSubject.create();
    public static PublishSubject<Event<Player>> gamePlayerSubject = PublishSubject.create();
    public static PublishSubject<Event<MessageDto>> gameChatSubject = PublishSubject.create();

    @Provides
    @Singleton
    AuthApiService authApiService(){
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
    @Singleton
    EventListener eventListener() {
        EventListener eventListener = mock(EventListener.class);

        when(eventListener.listen("users.*.*", User.class)).thenReturn(PublishSubject.create());
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(PublishSubject.create());
        when(eventListener.listen("games.000.members.*.*", Member.class)).thenReturn(gameMemberSubject);
        when(eventListener.listen("games.000.*", Game.class)).thenReturn(gameSubject);
        when(eventListener.listen("games.000.messages.*.*", MessageDto.class)).thenReturn(gameChatSubject);

        when(eventListener.listen("users.000.updated", User.class)).thenReturn(PublishSubject.create());
        when(eventListener.listen("users.001.updated", User.class)).thenReturn(PublishSubject.create());
        when(eventListener.listen("users.002.updated", User.class)).thenReturn(PublishSubject.create());
        when(eventListener.listen("users.003.updated", User.class)).thenReturn(PublishSubject.create());

        when(eventListener.listen("games.000.messages.*.*", MessageDto.class)).thenReturn(gameChatSubject);
        when(eventListener.listen("games.000.players.*.*", Player.class)).thenReturn(gamePlayerSubject);
        when(eventListener.listen("games.000.buildings.*.*", Building.class)).thenReturn(gameBuildingSubject);
        when(eventListener.listen("games.000.state.*", State.class)).thenReturn(gameStateSubject);
        when(eventListener.listen("games.000.moves.*.*", Move.class)).thenReturn(gameMoveSubject);

        return eventListener;
    }

    @Provides
    @Singleton
    Preferences prefs() {
        return Preferences.userNodeForPackage(Main.class);
    }

    @Provides
    @Singleton
    Retrofit retrofit (OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL+API_PREFIX+"/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .build();
    }

    @Provides
    @Singleton
    UserApiService userApiService(){
        return new UserApiService() {
            @Override
            public Observable<User> create(CreateUserDto dto) {
                return Observable.just(new User("000",dto.name(),"online",dto.avatar()));
            }

            @Override
            public Observable<User> getUser(String id) {

                return Observable.just(new User(id,"TestUser_" + id,"online",""));
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
    GameApiService gameApiService() {
        return new GameApiService() {
            @Override
            public Observable<List<Game>> getGames() {
                ArrayList<Game> games = new ArrayList<>();
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","TestGameA","001",1,false, null));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","002","TestGameB","002",1,false, null));
                games.add(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","003","TestGameC","003",1,false, null));
                return Observable.just(games);
            }

            @Override
            public Observable<Game> getGame(String id) {
                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestGameA","000",1,false, null));
            }

            @Override
            public Observable<Game> create(CreateGameDto dto) {
                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000",dto.name(),"000",1,false, new GameSettings(1,10)));
            }

            @Override
            public Observable<Game> update(String id, UpdateGameDto dto) {
                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","000",1,false, new GameSettings(2,10)));
            }

            @Override
            public Observable<Game> delete(String id) {
                return Observable.just(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestUserGame","000",1,false, null));
            }
        };
    }

    @Provides
    @Singleton
    MessageApiService messageApiService() {
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
    GroupApiService groupApiService() {
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
    GameMemberApiService gameMemberApiService() {
        return new GameMemberApiService() {
            @Override
            public Observable<List<Member>> getAll(String gameId) {
                ArrayList<Member> users = new ArrayList<>();
                return Observable.just(users);
            }

            @Override
            public Observable<Member> createMember(String gameId, CreateMemberDto dto) {
                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",false, "#ff0000",false));
            }

            @Override
            public Observable<Member> deleteMember(String gameId, String userId) {
                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",false, "#ff0000",false));
            }

            @Override
            public Observable<Member> patchMember(String gameId, String userId, UpdateMemberDto dto) {
                return Observable.just(new Member("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z",gameId,"000",true, "#ff0000",false));
            }
        };
    }

    @Provides
    NewGameLobbyService newGameLobbyService(){
        return new NewGameLobbyService(gameApiService(),gameMemberApiService(),messageApiService(), authApiService()){
            private String currentMemberId;

            public Observable<List<Member>> getAll(String id){
                return gameMemberApiService().getAll(id);
            }

            public Observable<Member> postMember(String id, boolean ready, String color, String password){
                return gameMemberApiService().createMember(id, new CreateMemberDto(ready, color, password));
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
                return gameMemberApiService().patchMember(groupId, userId, new UpdateMemberDto(true, "#ff0000",false));
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
    PrefService prefService() {
        return new PrefService(null, null,null, null) {
            @Override
            public String recall(){
                return "";
            }
            @Override
            public void remember() {
            }

            @Override
            public boolean getDarkModeState() {
                return true;
            }

            @Override
            public void saveDarkModeState(String string) {
            }

            @Override
            public Game getSavedGame() {
                return null;
            }
        };
    }

    @Provides
    @Singleton
    PioneersApiService pioneersApiService() {
        return new PioneersApiService() {
            @Override
            public Observable<Map> getMap(String gameId) {

                List<Tile> tiles = new ArrayList<>();
                tiles.add(new Tile(0,0,0,"desert",12));

                tiles.add(new Tile(0,1,-1,"pasture",2));
                tiles.add(new Tile(0,-1,1,"fields",3));
                tiles.add(new Tile(1,-1,0,"forest",4));
                tiles.add(new Tile(-1,1,0,"hills",5));
                tiles.add(new Tile(1,0,-1,"mountains",6));
                tiles.add(new Tile(-1,0,1,"pasture",7));

                tiles.add(new Tile(2,-1,-1,"pasture",2));
                tiles.add(new Tile(-2,1,1,"fields",3));
                tiles.add(new Tile(-1,-1,2,"forest",4));
                tiles.add(new Tile(1,1,-2,"hills",5));
                tiles.add(new Tile(-1,2,-1,"mountains",6));
                tiles.add(new Tile(1,-2,1,"pasture",7));
                tiles.add(new Tile(2,-2,0,"pasture",2));
                tiles.add(new Tile(-2,2,0,"fields",3));
                tiles.add(new Tile(0,2,-2,"forest",4));
                tiles.add(new Tile(0,-2,2,"hills",5));
                tiles.add(new Tile(2,0,-2,"mountains",6));
                tiles.add(new Tile(-2,0,2,"pasture",7));

                List<Harbor> harbors = new ArrayList<>();
                harbors.add(new Harbor(-1, -1, 2, "ore", 7));
                harbors.add(new Harbor(0, -2, 2, null, 5));
                harbors.add(new Harbor(1, -2, 1, "wool", 5));
                harbors.add(new Harbor(2, -1, -1, null, 3));
                harbors.add(new Harbor(2, 0, -2, "lumber", 1));
                harbors.add(new Harbor(1, 1, -2, null, 1));
                harbors.add(new Harbor(-1, 2, -1, "brick", 11));
                harbors.add(new Harbor(-2, 2, 0, null, 9));
                harbors.add(new Harbor(-2, 1, 1, "grain", 9));

                return Observable.just(new Map("000", tiles, harbors));
            }

            @Override
            public Observable<List<Player>> getAllPlayers(String gameId) {
                List<Player> players = new ArrayList<>();

                players.add(new Player("000","000","#ff0000", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0));
                players.add(new Player("000","001","#00ff00", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0));
                players.add(new Player("000","002","#0000ff", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0));
                players.add(new Player("000","003","#ffffff", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0));

                return Observable.just(players);
            }

            @Override
            public Observable<Player> getPlayer(String gameId, String userId) {
                Player player1 = new Player("000","000","#ff0000", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0);
                Player player2 = new Player("000","001","#00ff00", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0);
                Player player3 = new Player("000","002","#0000ff", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0);
                Player player4 = new Player("000","003","#ffffff", true,null, new Resources(0,0,0,0,0,0), new RemainingBuildings(5,4,15), 0, 0);

                if(userId.equals(player1.userId())) {
                    return Observable.just(player1);
                } else if (userId.equals(player2.userId())) {
                    return Observable.just(player2);
                } else if (userId.equals(player3.userId())) {
                    return Observable.just(player3);
                } else {
                    return Observable.just(player4);
                }

            }

            @Override
            public Observable<State> getCurrentState(String gameId) {
                ArrayList<String> players = new ArrayList<>();
                players.add("000");
                ArrayList<ExpectedMove> expectedMoves = new ArrayList<>();
                expectedMoves.add(new ExpectedMove("founding-roll",players));

                return Observable.just(new State("2022-06-09T15:11:51.795Z","000", expectedMoves, null));
            }

            @Override
            public Observable<List<Building>> getAllBuildings(String gameId) {
                return Observable.just(new ArrayList<>());
            }

            @Override
            public Observable<Building> getBuilding(String gameId, String buildingId) {
                return null;
            }

            @Override
            public Observable<Move> postMove(String gameId, CreateMoveDto dto) {
                if(dto.building() != null) {
                    return Observable.just(new Move("000", "2022-06-09T15:11:51.795Z", "000", "000", dto.action(), 1, dto.building().type(), dto.rob(), dto.resources(), dto.partner()));
                } else {
                    return Observable.just(new Move("000", "2022-06-09T15:11:51.795Z", "000", "000", dto.action(), 1, null, dto.rob(), dto.resources(), dto.partner()));
                }
            }

            @Override
            public Observable<Player> updatePlayer(String gameId, String userId, UpdatePlayerDto dto) {
                return Observable.just(new Player("000","000","#ff0000", true,1, new Resources(0,0,0,0,0,0),new RemainingBuildings(1,1,1), 0, 0));
            }
        };
    }
}

