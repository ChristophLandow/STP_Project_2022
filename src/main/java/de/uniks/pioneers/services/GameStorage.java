package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static de.uniks.pioneers.GameConstants.SETTLEMENT;

@Singleton
public class GameStorage {

    private List<Tile> map;

    @Inject
    EventListener eventListener;

    @Inject
    public GameStorage() {
    }

    public String selectedBuilding = "";

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

}
