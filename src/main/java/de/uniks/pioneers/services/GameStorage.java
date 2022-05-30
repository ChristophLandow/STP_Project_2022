package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Tile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameStorage {

    private List<Tile> map;

    @Inject
    public GameStorage() {
    }


    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }
}
