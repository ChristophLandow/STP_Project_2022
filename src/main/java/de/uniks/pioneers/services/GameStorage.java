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
    private List<Harbor> harbors;
    private int mapRadius;
    private  double hexScale = 75;
    private double hexRadiusFactor = 3;
    private double zoomedIn = 1.4;
    private double zoomedOut = 1;

    public String selectedBuilding = "";

    public ObservableMap<String, Integer> remainingBuildings = FXCollections.observableHashMap();

    @Inject
    public GameStorage() {
        System.out.println("Test");
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
        this.zoomedIn = 1 + (mapRadius)*0.2;

        switch(mapRadius){
            case 0:
                hexScale = 140;
                this.zoomedIn = 1;
                this.zoomedOut = 1;
                break;
            case 1:
                hexScale = 110;
                this.zoomedIn = 1;
                this.zoomedOut = 1;
                break;
            case 2:
                hexScale = 74;
                zoomedOut = 0.99;
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
                break;
        }

        if(mapRadius < 7){
            hexRadiusFactor = 5;
        }
        else{
            hexRadiusFactor = 2;
        }
    }
}
