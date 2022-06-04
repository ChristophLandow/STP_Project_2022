package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.model.Tile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameStorage {

    private final UserService userService;

    private List<Tile> map;
    public final ObservableList<Player> players = FXCollections.observableArrayList();
    public final ObservableList<Building> buildings = FXCollections.observableArrayList();
    public Player currentPlayer;
    public State currentState;


    @Inject
    public GameStorage(UserService userService) {
        this.userService = userService;
    }

    public void findMe() {
        currentPlayer = players.stream()
                .filter(player -> userService.getCurrentUser()._id().equals(player.userId())).findFirst().orElse(null);
    }

    public boolean checkRoadSpot(int x, int y, int z) {
        return buildings.stream().anyMatch(building -> building.x() == x && building.y() == y && building.z() == z
                && building.owner().equals(currentPlayer.userId())
                // the last conjunct is redundant but might be usefull later
                && building.type().equals("settlement"));
    }

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }
}
