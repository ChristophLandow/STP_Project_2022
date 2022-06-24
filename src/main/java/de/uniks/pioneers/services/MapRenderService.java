package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTileController;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Objects;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class MapRenderService {

    private final GameStorage gameStorage;
    private ArrayList<HexTileController> tileControllers = new ArrayList<>();

    private Canvas mapCanvas;

    private GraphicsContext gc;

    private boolean finishedLoading = false;

    @Inject
    MapRenderService(GameStorage gameStorage){
        this.gameStorage = gameStorage;
    }

    public void setTileControllers(ArrayList<HexTileController> tileControllers) {
        this.tileControllers = tileControllers;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
    }

    public void setMapCanvas(Canvas mapCanvas) {
        this.mapCanvas = mapCanvas;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public Canvas getMapCanvas() {
        return mapCanvas;
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
            //hexTileController.setVisible(isOnScreen(hexTileController.getView(),gameStorage.getHexScale()*gameStorage.getHexRadiusFactor()));
            hexTileController.setVisible(isOnScreen(hexTileController.getView(),-10));
        }
    }

    private boolean isOnScreen(Node node, double radius){
        Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
        return boundsInScene.getCenterX() >= MAP_X - radius && boundsInScene.getCenterX() <= MAP_X+MAP_WIDTH+radius && boundsInScene.getCenterY() >= MAP_Y-radius && boundsInScene.getCenterY() <= MAP_Y+MAP_HEIGHT+radius;
    }

    public void stop(){
        this.tileControllers.clear();
    }
}
