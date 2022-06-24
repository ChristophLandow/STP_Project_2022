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

    public void calcZoom(int mapRadius){
        this.mapRadius = mapRadius;

        switch(mapRadius){
            case 0:
                this.hexScale = 180;
                this.zoomedIn = -1;
                break;
            case 1:
                this.hexScale = 100;
                this.zoomedIn = -1;
                break;
            case 12:
            case 13:
                this.hexScale = 120;
                this.zoomedIn = 1;
                this.zoomedOut = -1;
                break;
            default:
                this.hexScale = 120;
                double hexagonHeight = 2 * this.hexScale;
                double mapHeight = (2*this.mapRadius + 1) * hexagonHeight + MAP_PADDING * 2;
                this.zoomedOut = (MAP_HEIGHT/mapHeight);
                this.zoomedIn = 1;
                break;
            /*case 2:
                hexScale = 120;
                zoomedIn = 1;
                zoomedOut = 0.6;
                break;
            case 3:
                hexScale = 52;
                break;
            case 4:
                hexScale = 40;
                break;
            case 5:
                hexScale = 33;
                break;
            case 6:
                hexScale = 28;
                break;
            case 7:
            case 8:
            case 9:
                hexScale = 18;
                zoomedOut = -1;
                zoomedIn = 5;
                break;
            case 10:
                hexScale = 16;
                zoomedOut = -1;
                zoomedIn = 5;
                break;*/
        }

        if(mapRadius < 7){
            hexRadiusFactor = 5;
        }
        else{
            hexRadiusFactor = 2;
        }
    }
}
