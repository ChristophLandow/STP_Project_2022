package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.FOUNDING_ROAD_1;
import static de.uniks.pioneers.GameConstants.FOUNDING_ROAD_2;

public class StreetPointController {

    private final GameStorage gameStorage;
    private final IngameService ingameService;
    public HexTile tile;
    private Circle view;

    private final CompositeDisposable disposable = new CompositeDisposable();
    //coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];

    public ArrayList<BuildingPointController> buildings = new ArrayList<>();
    SimpleIntegerProperty side = new SimpleIntegerProperty();

    @Inject
    public StreetPointController(GameStorage gameStorage, IngameService ingameService) {
        this.gameStorage = gameStorage;
        this.ingameService = ingameService;
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


        if (move.players().get(0).equals(gameStorage.me.userId())) {
            if ((move.action().equals(FOUNDING_ROAD_1) || move.action().equals(FOUNDING_ROAD_2))) {
                if (buildings.stream().anyMatch(c -> gameStorage.checkRoadSpot(c.tile.q, c.tile.r, c.tile.s))) {
                    System.out.println("baue straße von feld aus ");
                    determineSide();
                    CreateBuildingDto newBuilding = new CreateBuildingDto(tile.q, tile.r, tile.s, side.get(), "road");
                    disposable.add(ingameService.postMove(gameStorage.game.get()._id(), new CreateMoveDto(move.action(), newBuilding))
                            .observeOn(FX_SCHEDULER)
                            .subscribe());
                }
            }
        }
    }

    public void renderRoad(Building building) {
        /*final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.valueOf(gameStorage.currentPlayer.color()));
        redMaterial.setSpecularColor(Color.valueOf(gameStorage.currentPlayer.color()));
        box.setMaterial(redMaterial);*/
        System.out.println("render road building ");
        Player player = gameStorage.players.get(building.owner());
        side.set(building.side());
        Rectangle road =  new Rectangle(60,7, Paint.valueOf(player.color()));
        Scene scene = view.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(road);
        road.setLayoutX(view.getLayoutX()-14);
        road.setLayoutY(view.getLayoutY()+12);
        if (side.get() == 3) {
            road.setRotate(90);
        } else if (side.get() == 7) {
            road.setRotate(30);
        }else {
            road.setRotate(-30);
        }
    }


    public void renderRoad() {
        /*final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.valueOf(gameStorage.currentPlayer.color()));
        redMaterial.setSpecularColor(Color.valueOf(gameStorage.currentPlayer.color()));
        box.setMaterial(redMaterial);*/
        System.out.println(" render road");
        Rectangle road =  new Rectangle(60,7, Paint.valueOf(gameStorage.me.color()));
        Scene scene = view.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(road);
        road.setLayoutX(view.getLayoutX()-14);
        road.setLayoutY(view.getLayoutY()+12);
        if (side.get() == 3) {
            road.setRotate(90);
        } else if (side.get() == 7) {
            road.setRotate(30);
        }else {
            road.setRotate(-30);
        }
    }

    private void determineSide() {
        BuildingPointController neighbor = buildings.get(0);
        BuildingPointController neighborOther = buildings.get(1);
        if (neighbor.tile.q == neighborOther.tile.s || neighborOther.tile.q == neighbor.tile.s) {
            side.set(3);
        } else if (neighbor.tile.q == neighborOther.tile.r || neighborOther.tile.q == neighbor.tile.r) {
            side.set(7);
        } else {
            side.set(11);
        }
    }


    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(0, 255, 0));
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(255, 0, 0));
    }

    public String generateKeyString() {
        return uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3];
    }

    public ArrayList<BuildingPointController> getBuildings() {
        return this.buildings;
    }


}
