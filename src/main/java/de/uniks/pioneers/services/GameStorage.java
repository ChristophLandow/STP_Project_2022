package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Tile;
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

    private boolean customMap;
    private int mapRadius;
    private double hexScale = 75;
    private double hexRadiusFactor = 3;
    private double zoomedIn = 1.4;
    private double zoomedOut = 1;

    private double fieldPaneMoveChildrenX;
    private double fieldPaneMoveChildrenY;
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

    public void setHexScale(double hexScale) {
        this.hexScale = hexScale;
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

    public double getFieldPaneMoveChildrenX() {
        return fieldPaneMoveChildrenX;
    }

    public double getFieldPaneMoveChildrenY() {
        return fieldPaneMoveChildrenY;
    }

    public boolean isCustomMap() {
        return customMap;
    }

    public void setMap(List<Tile> map) {
        this.map = map;

        if(customMap){
            calcMapSize();
        }
    }

    public void setHarbors(List<Harbor> harbors) {
        this.harbors = harbors;
    }

    public void setZoomedOut(double zoomedOut) {
        this.zoomedOut = zoomedOut;
    }

    public void setFieldPaneMoveChildrenX(double fieldPaneMoveChildrenX) {
        this.fieldPaneMoveChildrenX = fieldPaneMoveChildrenX;
    }

    public void setFieldPaneMoveChildrenY(double fieldPaneMoveChildrenY) {
        this.fieldPaneMoveChildrenY = fieldPaneMoveChildrenY;
    }

    public void calcZoom(int mapRadius, boolean customMap){
        this.customMap = customMap;

        if(customMap){
            mapRadius = 25;
        }

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

    private void calcMapSize(){
        int newMapRadius = 0;

        for(Tile tile : map){
            int maxVal = Math.max(Math.abs(tile.x()), Math.abs(tile.y()));
            maxVal = Math.max(maxVal, Math.abs(tile.z()));

            if(maxVal > newMapRadius){
                newMapRadius = maxVal;
            }
        }

        this.mapRadius = newMapRadius;

        double hexagonHeight = 2 * hexScale;
        double mapHeight;
        if (mapRadius % 2 == 0) {
            mapHeight = (mapRadius + 1) * hexagonHeight + mapRadius * hexScale + MAP_PADDING_Y + hexScale;
        } else {
            mapHeight = mapRadius * hexagonHeight + (mapRadius + 1) * hexScale + hexScale + MAP_PADDING_Y + hexScale;
        }
        this.zoomedOut = (MAP_HEIGHT / mapHeight);
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
