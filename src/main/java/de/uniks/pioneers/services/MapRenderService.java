package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTileController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class MapRenderService {

    private final GameStorage gameStorage;
    private ArrayList<HexTileController> tileControllers = new ArrayList<>();

    private GraphicsContext gc;

    private final SimpleBooleanProperty finishedLoading = new SimpleBooleanProperty(false);

    @Inject
    MapRenderService(GameStorage gameStorage){
        this.gameStorage = gameStorage;
    }

    public void setTileControllers(ArrayList<HexTileController> tileControllers) {
        this.tileControllers = tileControllers;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading.set(finishedLoading);
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public SimpleBooleanProperty isFinishedLoading() {
        return finishedLoading;
    }

    public ArrayList<HexTileController> getTileControllers() {
        return tileControllers;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public int calcSleepHexagon(){
        if(gameStorage.getMapRadius() > 7){
            return 40;
        }
        else  if(gameStorage.getMapRadius() >= 4){
            return 20;
        }
        else{
            return 10;
        }
    }

    public void checkPoints(){
        for(HexTileController hexTileController: tileControllers){
            hexTileController.setVisible(isOnScreen(hexTileController.getView(),gameStorage.getHexScale()*gameStorage.getHexRadiusFactor()));
        }
    }

    private boolean isOnScreen(Node node, double radius){
        Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
        return boundsInScene.getCenterX() >= MAP_X - radius && boundsInScene.getCenterX() <= MAP_X+MAP_WIDTH+radius && boundsInScene.getCenterY() >= MAP_Y-radius && boundsInScene.getCenterY() <= MAP_Y+MAP_HEIGHT+radius;
    }

    public void stop(){
        this.tileControllers.clear();
        this.finishedLoading.set(false);
    }
}
