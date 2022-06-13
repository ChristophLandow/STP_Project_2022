package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameStorage {

    private List<Tile> map;

    @Inject
    EventListener eventListener;

    @Inject
    public GameStorage() {
    }

//    public void setCurrentPlayers(List<String> playerIdS) {
//        currentPlayers = players.values().stream().filter(player -> playerIdS.contains(player.userId())).toList();
//    }

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

}
