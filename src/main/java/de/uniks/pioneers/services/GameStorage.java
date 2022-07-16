package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class GameStorage {
    @Inject EventListener eventListener;
    private List<Tile> map;
    private List<Harbor> harbors;
    public List<String> tradeOptions = new ArrayList<>();
    private int mapRadius;
    private  double hexScale = 75;
    private double hexRadiusFactor = 3;
    private double zoomedIn = 1.4;
    private double zoomedOut = 1;
    public String selectedBuilding = "";
    public ObservableMap<String, Integer> remainingBuildings = FXCollections.observableHashMap();

    @Inject
    public GameStorage() {
        remainingBuildings.put(ROAD, 15);
        remainingBuildings.put(SETTLEMENT, 5);
        remainingBuildings.put(CITY, 4);
    }

    public List<Tile> getMap() {
        return map;
    }

    public List<Harbor> getHarbors() { return harbors; }

    public int getMapRadius(){
        return this.mapRadius;
    }

    public double getHexScale() {
        return hexScale;
    }

    public double getHexRadiusFactor() {
        return hexRadiusFactor;
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

    public void setHarbors(List<Harbor> harbors) {
        this.harbors = harbors;
    }

    public void calcZoom(int mapRadius){
        this.mapRadius = mapRadius;
        hexRadiusFactor = 5;

        switch (mapRadius) {
            case 0 -> {
                this.hexScale = 180;
                this.zoomedIn = -1;
            }
            case 1 -> {
                this.hexScale = 100;
                this.zoomedIn = -1;
            }
            default -> {
                this.hexScale = 120;
                double hexagonHeight = 2 * hexScale;
                double mapHeight;
                if (mapRadius % 2 == 0) {
                    mapHeight = (mapRadius + 1) * hexagonHeight + mapRadius * hexScale + MAP_PADDING_Y + hexScale;
                } else {
                    mapHeight = mapRadius * hexagonHeight + (mapRadius + 1) * hexScale + hexScale + MAP_PADDING_Y + hexScale;
                }
                this.zoomedOut = (MAP_HEIGHT / mapHeight);
                this.zoomedIn = 1;
            }
        }

        if(mapRadius >= 4){
            this.zoomedIn = 0.7;
        }
    }

    public void addToTradeOptions(String tradeOption) {
        if (!this.tradeOptions.contains(tradeOption)) {
            this.tradeOptions.add(tradeOption);
        }
    }

    public void resetRemainingBuildings() {
        remainingBuildings = FXCollections.observableHashMap();

        remainingBuildings.put(ROAD, 15);
        remainingBuildings.put(SETTLEMENT, 5);
        remainingBuildings.put(CITY, 4);
    }
}
