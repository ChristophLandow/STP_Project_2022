package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class GameStorage {
    @Inject EventListener eventListener;
    private List<Tile> map;

    private double zoomedIn = 1.3;
    private double zoomedOut = 0.99;

    @Inject
    public GameStorage() {
        remainingBuildings.put(ROAD, 15);
        remainingBuildings.put(SETTLEMENT, 5);
        remainingBuildings.put(CITY, 4);
    }

    public String selectedBuilding = "";

    public ObservableMap<String, Integer> remainingBuildings = FXCollections.observableHashMap();

    public List<Tile> getMap() {
        return map;
    }

    public double getZoomedIn(){
        return this.zoomedIn;
    }

    public double getZoomedOut(){
        return this.zoomedOut;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

    public void setZoom(double zoomedIn, double zoomedOut){
        this.zoomedIn = zoomedIn;
        this.zoomedOut = zoomedOut;
    }
}
