package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTileController;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class MapRenderService {

    private final GameStorage gameStorage;
    private ArrayList<HexTileController> tileControllers = new ArrayList<>();

    @Inject
    MapRenderService(GameStorage gameStorage){
        this.gameStorage = gameStorage;
    }

    public void setTileControllers(ArrayList<HexTileController> tileControllers) {
        this.tileControllers = tileControllers;
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
    }
}
