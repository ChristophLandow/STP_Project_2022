package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class GameStorage {

    private final UserService userService;

    private List<Tile> map;
    public final ObservableMap<String, Player> players = FXCollections.observableHashMap();
    public final ObservableList<Building> buildings = FXCollections.observableArrayList();
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public Player me;
    public List<Player> currentPlayers;
    public State currentState;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    EventListener eventListener;

    @Inject
    public GameStorage(UserService userService) {
        this.userService = userService;
    }

    public void initPlayerListener() {
        String patternToObservePlayers = String.format("games.%s.players.*", game.get()._id());
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

    public void findMe() {
        me = players.get(userService.getCurrentUser()._id());
        assert me != null;
        System.out.println("Player id " + me.userId());
    }

    public boolean checkRoadSpot(int x, int y, int z) {
        return buildings.stream().anyMatch(building -> building.x() == x && building.y() == y && building.z() == z
                && building.owner().equals(me.userId())
                && building.type().equals("settlement"));
    }

    public void setCurrentPlayers(List<String> playerIdS) {
        currentPlayers = players.values().stream().filter(player -> playerIdS.contains(player.userId())).toList();
        assert currentPlayers != null;
        System.out.println("Current players " + currentPlayers);
    }

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

}
