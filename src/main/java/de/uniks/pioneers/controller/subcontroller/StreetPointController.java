package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
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
import static de.uniks.pioneers.GameConstants.*;

public class StreetPointController {
    private final GameService gameService;
    private final IngameService ingameService;
    private final UserService userService;

    private final GameStorage gameStorage;
    private Pane fieldPane;
    public HexTile tile;
    private Circle view;
    private Circle eventView;
    private final CompositeDisposable disposable = new CompositeDisposable();
    // coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];
    public ArrayList<BuildingPointController> adjacentBuildings = new ArrayList<>();
    private String action;
    private Building building;

    @Inject
    public StreetPointController(GameService gameService, IngameService ingameService, UserService userService, GameStorage gameStorage) {
        this.gameService = gameService;
        this.ingameService = ingameService;
        this.userService = userService;
        this.gameStorage = gameStorage;
    }

    public void post(HexTile tile, Circle view, Pane fieldPane) {
        this.tile = tile;
        this.view = view;
        this.fieldPane = fieldPane;

        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(15);
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
            disposable.add(ingameService.postMove(gameService.game.get()._id(), new CreateMoveDto(this.action, newBuilding))
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
        Player player = gameService.players.get(building.owner());
        Rectangle road = new Rectangle(60, 7, Paint.valueOf(player.color()));
        Scene scene = view.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(road);
        road.setLayoutX(view.getLayoutX() - 14);
        road.setLayoutY(view.getLayoutY() + 12);
        if (building.side() == 3) {
            road.setRotate(90);
        } else if (building.side() == 7) {
            road.setRotate(30);
        } else {
            road.setRotate(-30);
        }
        this.building = building;
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

    public ArrayList<BuildingPointController> getAdjacentBuildings() {
        return this.adjacentBuildings;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
