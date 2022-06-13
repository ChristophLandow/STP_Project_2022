package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.controller.subcontroller.BuildingPointController;
import de.uniks.pioneers.controller.subcontroller.HexTileController;
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
import javax.inject.Provider;
import javax.inject.Singleton;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.SETTLEMENT;
import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class GameService {

    public final ObservableMap<String, Player> players = FXCollections.observableHashMap();
    public final ObservableList<Building> buildings = FXCollections.observableArrayList();
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public Player me;
    public final ObservableList<Player> currentPlayers = FXCollections.observableArrayList();
    public final ObservableList<Move> moves = FXCollections.observableArrayList();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final GameApiService gameApiService;

    private final UserService userService;
    private final IngameService ingameService;



    @Inject
    EventListener eventListener;




    @Inject
    public GameService(GameApiService gameApiService, UserService userService, IngameService ingameService) {
        this.gameApiService = gameApiService;
        this.userService = userService;
        this.ingameService = ingameService;
    }

    public Observable<Game> deleteGame(String gameId) {
        return gameApiService.delete(gameId);
    }

    public void initGame() {
        // REST - get list of all players from server and set current player
        disposable.add(ingameService.getAllPlayers(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(list -> {
                            list.forEach(player -> {
                                players.put(player.userId(), player);
                            });
                            findMe();
                        }
                        , Throwable::printStackTrace));

        // REST - get buildings from server
        disposable.add(ingameService.getAllBuildings(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(buildings::setAll,
                        Throwable::printStackTrace));

        // init players listener
        this.initPlayerListener();
        this.initBuildingsListener();
        this.initMoveListener();
    }

    private void initMoveListener() {
        String patternToObserveMoves = String.format("games.%s.moves.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveMoves, Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(moveEvent -> {
                    final Move move = moveEvent.data();
                    System.out.println(move);
                    if (moveEvent.event().endsWith(".created")) {
                        this.moves.add(move);
                        handleMove(move);
                    }
                })
        );
    }

    private void handleMove(Move move){
        switch (move.action()) {
            case ROLL -> distributeResources(move);
            case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> {
                //update victory points
            }
            case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> {
                //update longest road
            }
            case BUILD -> {
                // update longest road, check longest road
                // update victory points, check winner
            }
        }
    }

    private void distributeResources(Move move) {
        if (move.roll()!=7){
            HexTileController hexTileController = findHexTile(move.roll());
            List<BuildingPointController> buildingPointControllers = Arrays.stream(hexTileController.corners).toList();
            buildingPointControllers.forEach(controller -> {
                assert controller.getBuilding()!=null;
                Building building = controller.getBuilding();
                String type = hexTileController.tile.type;
                Player owner = this.players.get(controller.getBuilding().owner());
                Resources resources;
                if (building.type().equals(SETTLEMENT)){
                    resources = owner.resources().updateResources(type, 1);
                }else {
                    resources = owner.resources().updateResources(type, 2);
                }
                Player updatedPlayer = new Player(owner.gameId(), owner.userId(),owner.color(),owner.foundingRoll(),resources,owner.remainingBuildings());
                players.replace(owner.userId(),updatedPlayer);
            });
        }
    }


    private void initPlayerListener() {
        String patternToObservePlayers = String.format("games.%s.players.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObservePlayers, Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    Player player = gameEvent.data();
                    String id = player.userId();
                    System.out.println("player updated event");
                    if (gameEvent.event().endsWith(".updated")) {
                        players.replace(id, players.get(id), player);
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        players.remove(id);
                    }
                })
        );
    }

    private void initBuildingsListener() {
        String patternToObserveBuildings = String.format("games.%s.buildings.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveBuildings, Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(buildingEvent -> {
                    final Building building = buildingEvent.data();
                    if (buildingEvent.event().endsWith(".created")) {
                        // render new building
                        this.buildings.add(building);
                    }
                })
        );
    }

    public void findMe() {
        me = players.get(userService.getCurrentUser()._id());
    }

    public boolean checkRoadSpot(int x, int y, int z) {
        return buildings.stream().anyMatch(building -> building.x() == x && building.y() == y && building.z() == z
                && building.owner().equals(me.userId())
                && building.type().equals("settlement"));
    }

}
