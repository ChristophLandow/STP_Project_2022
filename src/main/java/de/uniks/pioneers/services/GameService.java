package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class GameService {
    public ObservableMap<String, Player> players = FXCollections.observableHashMap();
    public ObservableList<Member> members = FXCollections.observableArrayList();
    public ObservableList<Member> lobbyMembers;
    public final ObservableList<Building> buildings = FXCollections.observableArrayList();
    public final ObservableList<Move> moves = FXCollections.observableArrayList();
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final GameApiService gameApiService;
    public String me;
    private final UserService userService;
    private final IngameService ingameService;
    private final NewGameLobbyService newGameLobbyService;
    @Inject EventListener eventListener;

    @Inject
    public GameService(GameApiService gameApiService, UserService userService, IngameService ingameService, NewGameLobbyService newGameLobbyService) {
        this.gameApiService = gameApiService;
        this.userService = userService;
        this.ingameService = ingameService;
        this.newGameLobbyService = newGameLobbyService;
    }

    public Observable<Game> deleteGame(String gameId) {
        return gameApiService.delete(gameId);
    }

    public void initGame() {
        // REST - get buildings from server
        disposable.add(ingameService.getAllBuildings(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(buildings::setAll,
                        Throwable::printStackTrace));

        // init players listener
        this.initPlayerListener();
        this.initMemberListener();
        this.initBuildingsListener();
        this.initMoveListener();
    }

    private void initMoveListener() {
        String patternToObserveMoves = String.format("games.%s.moves.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveMoves, Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(moveEvent -> {
                    final Move move = moveEvent.data();
                    if (moveEvent.event().endsWith(".created")) {
                        this.moves.add(move);

                    }
                })
        );
    }

    private void initPlayerListener() {
        String patternToObservePlayers = String.format("games.%s.players.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObservePlayers, Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    Player player = gameEvent.data();
                    String id = player.userId();
                    if (gameEvent.event().endsWith(".updated")) {
                        players.replace(id, players.get(id), player);
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        players.remove(id);
                    }
                })
        );
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    if (memberEvent.event().endsWith(".created")) {
                        members.add(member);
                    } else if (memberEvent.event().endsWith(".updated")) {
                        members.replaceAll(m -> m.userId().equals(member.userId()) ? member : m);
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(member);
                    }
                }));
    }

    private void initBuildingsListener() {
        String patternToObserveBuildings = String.format("games.%s.buildings.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveBuildings, Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(buildingEvent -> {
                    final Building building = buildingEvent.data();
                    if (buildingEvent.event().endsWith(".created") || buildingEvent.event().endsWith(".updated")) {
                        // render new building
                        this.buildings.add(building);
                    }
                })
        );
    }

    public void loadPlayers(Game game) {
        // REST - get players from server
        players = FXCollections.observableHashMap();

        disposable.add(ingameService.getAllPlayers(game._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(list -> {
                    list.forEach(player -> players.put(player.userId(), player));
                    System.out.println(members);
                    members.addAll(lobbyMembers);
                    System.out.println(members);
                    me = userService.getCurrentUser()._id();
                    }, Throwable::printStackTrace));
    }

    public boolean checkRoadSpot(int x, int y, int z, int side) {
        return buildings.stream().anyMatch(building -> building.owner().equals(me)
                && building.type().equals("road")
                && building.x() == x && building.y() == y && building.z() == z && building.side() == side);
    }

    public boolean isValidFromThree(int[] uploadCoords) {
        return checkRoadSpot(uploadCoords[0]+1,uploadCoords[1],uploadCoords[2]-1,7)
                || checkRoadSpot(uploadCoords[0]+1, uploadCoords[1]-1,uploadCoords[2],11)
                || checkRoadSpot(uploadCoords[0], uploadCoords[1]-1, uploadCoords[2]+1,11)
                || checkRoadSpot(uploadCoords[0]+1,uploadCoords[1]-1,uploadCoords[2],7);
    }

    public boolean isValidFromSeven(int[] uploadCoords) {
        return checkRoadSpot(uploadCoords[0]-1, uploadCoords[1],uploadCoords[2]+1,11)
                || checkRoadSpot(uploadCoords[0]-1, uploadCoords[1]+1,uploadCoords[2],3)
                || checkRoadSpot(uploadCoords[0], uploadCoords[1]-1, uploadCoords[2]+1,11)
                || checkRoadSpot(uploadCoords[0]-1,uploadCoords[1],uploadCoords[2]+1,3);
    }

    public boolean isValidFromEleven(int[] uploadCoords) {
        return checkRoadSpot(uploadCoords[0]-1, uploadCoords[1]+1,uploadCoords[2],3)
                || checkRoadSpot(uploadCoords[0], uploadCoords[1]+1,uploadCoords[2]-1,7)
                || checkRoadSpot(uploadCoords[0], uploadCoords[1]+1, uploadCoords[2]-1,3)
                || checkRoadSpot(uploadCoords[0]+1,uploadCoords[1],uploadCoords[2]-1,7);
    }

    public boolean checkBuildingSpot(int x, int y, int z, int side) {
        return buildings.stream().anyMatch(building -> building.owner().equals(me)
                && !building.type().equals("road")
                && building.x() == x && building.y() == y && building.z() == z && building.side() == side);
    }

    public boolean checkBuildingsFromThree(int[] uploadCoords) {
        return checkBuildingSpot(uploadCoords[0]+1,uploadCoords[1],uploadCoords[2]-1,6)
                || checkBuildingSpot(uploadCoords[0],uploadCoords[1]-1,uploadCoords[2]+1,0);
    }

    public boolean checkBuildingsFromSeven(int[] uploadCoords) {
        return checkBuildingSpot(uploadCoords[0]-1,uploadCoords[1],uploadCoords[2]+1,0)
                || checkBuildingSpot(uploadCoords[0],uploadCoords[1],uploadCoords[2],6);
    }

    public boolean checkBuildingsFromEleven(int[] uploadCoords) {
        return checkBuildingSpot(uploadCoords[0],uploadCoords[1],uploadCoords[2],0)
                || checkBuildingSpot(uploadCoords[0],uploadCoords[1]+1,uploadCoords[2]-1,6);
    }

    public boolean checkRoad() {
        Player mario = players.get(me);
        try {
            return mario.resources().lumber() >= 1 && mario.resources().brick() >= 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkSettlement() {
        Player mario = players.get(me);
        try {
            return mario.resources().lumber() >= 1 && mario.resources().brick() >= 1
                    && mario.resources().grain() >= 1 && mario.resources().wool() >= 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkCity() {
        Player mario = players.get(me);
        try {
            return mario.resources().ore() >= 3 && mario.resources().grain() >= 2;
        } catch (Exception e) {
            return false;
        }
    }

    public void setMembers(ObservableList<Member> lobbyMembers) {
        this.lobbyMembers = lobbyMembers;
    }

    public Game getGame() {
        return game.get();
    }
}
