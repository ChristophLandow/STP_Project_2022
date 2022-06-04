package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.services.GameStorage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.util.ArrayList;

import static de.uniks.pioneers.GameConstants.*;

public class StreetPointController {

    private final GameStorage gameStorage;
    public HexTile tile;
    private Circle view;

    public ArrayList<BuildingPointController> buildings = new ArrayList<>();

    @Inject
    public StreetPointController(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    public void post(HexTile tile, Circle view) {
        this.tile = tile;
        this.view = view;
        init();
    }

    public void init() {
        this.view.setOnMouseClicked(this::placeStreet);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);

    }

    private void placeStreet(MouseEvent mouseEvent) {
        ExpectedMove move = gameStorage.currentState.expectedMoves().get(0);

        if (move.players().get(0).equals(gameStorage.currentPlayer.userId())) {
            if ((move.action().equals(FOUNDING_ROAD_1) || move.action().equals(FOUNDING_ROAD_2))) {
                buildings.stream().anyMatch(c -> gameStorage.checkRoadSpot(c.tile.q,c.tile.r,c.tile.s));
            }
            //render street main phase

        } else {
            return;
        }
    }



    public void build() {
        //assigns building
    }

    public ArrayList<BuildingPointController> getBuildings() {
        return this.buildings;
    }

    private void info(MouseEvent mouseEvent) {
        for (BuildingPointController buildingPointController : this.buildings) {
            buildingPointController.mark();
        }
        System.out.println(tile);
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(0, 255, 0));
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(255, 0, 0));
    }

    public void mark() {
        this.view.setFill(Color.rgb(0, 0, 255));
    }

}
