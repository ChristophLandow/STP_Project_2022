package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class GameStorage {

    private final UserService userService;

    private List<Tile> map;
    public final ObservableMap<String, Player> players = FXCollections.observableHashMap();
    public final ObservableList<Building> buildings = FXCollections.observableArrayList();
    public SimpleObjectProperty <Game> game = new SimpleObjectProperty<>();
    public Player me;
    public List<Player> currentPlayers;
    public State currentState;


    @Inject
    public GameStorage(UserService userService) {
        this.userService = userService;
    }

    public void findMe() {
        me = players.get(userService.getCurrentUser()._id());
        assert me != null;
        System.out.println("Player id" + me.userId());
    }

    public boolean checkRoadSpot(int x, int y, int z) {
        return buildings.stream().anyMatch(building -> building.x() == x && building.y() == y && building.z() == z
                && building.owner().equals(me.userId())
                // the last conjunct is redundant but might be usefull later
                && building.type().equals("settlement"));
    }

    public void setCurrentPlayers(List<String> playerIdS) {
        currentPlayers = players.values().stream().filter(player -> playerIdS.contains(player.userId())).toList();
    }

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

}
