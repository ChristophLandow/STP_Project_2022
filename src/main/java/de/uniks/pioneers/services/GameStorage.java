package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameStorage {
    @Inject EventListener eventListener;
    private List<Tile> map;

    @Inject
    public GameStorage() {
    }

    public String selectedBuilding = "";

    public int roadsRemaining = 15;
    public int settlementsRemaining = 5;
    public int citiesRemaining = 4;

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }
}
