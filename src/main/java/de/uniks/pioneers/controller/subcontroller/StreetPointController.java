package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.MapRenderService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

import javax.inject.Inject;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class StreetPointController {
    private final GameService gameService;
    private final IngameService ingameService;

    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;
    private Pane fieldPane;
    public HexTile tile;
    private Circle view;
    private Circle eventView;
    private final CompositeDisposable disposable = new CompositeDisposable();
    // coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];
    public ArrayList<BuildingPointController> adjacentBuildings = new ArrayList<>();
    private String action;

    @Inject
    public StreetPointController(GameService gameService, IngameService ingameService, GameStorage gameStorage, MapRenderService mapRenderService) {
        this.gameService = gameService;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
    }

    public void post(HexTile tile, Circle view, Pane fieldPane) {
        this.tile = tile;
        this.view = view;
        this.fieldPane = fieldPane;

        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(gameStorage.getHexScale()/5);
        this.eventView.setOpacity(0);
    }

    public void init() {
        this.eventView.setOnMouseClicked(this::placeStreet);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }

    public void addEventArea() {
        this.fieldPane.getChildren().add(eventView);
    }

    public void placeStreet(MouseEvent mouseEvent) {
        System.out.println(generateKeyString());
        boolean valid;

        if (action.equals(FOUNDING_ROAD_1) || action.equals(FOUNDING_ROAD_2)) {
            valid = checkBuildings();
        } else {
            if (gameStorage.remainingBuildings.get(ROAD) >= 1 && gameService.checkRoad()) {
                valid = checkRoads() || checkBuildings();
            }else {
                valid = false;
            }
        }

        if (valid) {
            gameStorage.remainingBuildings.put(ROAD, gameStorage.remainingBuildings.get(ROAD)-1 );
            CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], "road");
            disposable.add(ingameService.postMove(gameService.game.get()._id(), new CreateMoveDto(this.action, null, null, null, newBuilding))
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                        Pane fieldPane = (Pane) this.view.getScene().getRoot().lookup("#fieldPane");
                        fieldPane.getChildren().forEach(this::reset);
                    }));
        }
    }

    private boolean checkBuildings() {
        if (uploadCoords[3] == 3) {
            return gameService.checkBuildingsFromThree(this.uploadCoords);
        } else if (uploadCoords[3] == 7) {
            return gameService.checkBuildingsFromSeven(this.uploadCoords);
        } else {
            return gameService.checkBuildingsFromEleven(this.uploadCoords);
        }
    }

    private Boolean checkRoads() {
        if (uploadCoords[3] == 3) {
            return gameService.isValidFromThree(this.uploadCoords);
        } else if (uploadCoords[3] == 7) {
            return gameService.isValidFromSeven(this.uploadCoords);
        } else {
            return gameService.isValidFromEleven(this.uploadCoords);
        }
    }

    private void reset(Node node) {
        node.setOnMouseClicked(null);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    public void renderRoad(Building building) {
        if(this.view.getRadius() != 0) {
            Player player = gameService.players.get(building.owner());
            double centerX = tile.x + this.fieldPane.getPrefWidth() / 2;
            double centerY = -tile.y + this.fieldPane.getPrefHeight() / 2;

            double streetWidth = this.gameStorage.getHexScale() / 1.25;
            double streetHeight = this.gameStorage.getHexScale() / 8.3;

            mapRenderService.getGc().save();

            if (building.side() == 3) {
                mapRenderService.getGc().transform(new Affine(new Rotate(90, centerX, centerY)));
            } else if (building.side() == 7) {
                mapRenderService.getGc().transform(new Affine(new Rotate(30, centerX, centerY)));
            } else {
                mapRenderService.getGc().transform(new Affine(new Rotate(-30, centerX, centerY)));
            }

            mapRenderService.getGc().setFill(Paint.valueOf(player.color()));
            mapRenderService.getGc().fillRect(centerX - streetWidth / 2, centerY - streetHeight / 2, streetWidth, streetHeight);

            mapRenderService.getGc().restore();

            this.reset(this.eventView);
            this.view.setVisible(false);
            this.view.setRadius(0);
        }
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(HOVER_COLOR);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(STANDARD_COLOR);
    }

    public String generateKeyString() {
        return uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3];
    }

    public ArrayList<BuildingPointController> getAdjacentBuildings() {
        return this.adjacentBuildings;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean alreadyPlacedStreet(){
        return this.view.getRadius() == 0;
    }

    public void setVisible(boolean isVisible){
        if(this.view.getRadius() != 0) {
            this.view.setVisible(isVisible);
            this.view.setDisable(!isVisible);
        }
    }
}
