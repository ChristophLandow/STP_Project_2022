package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.TimerService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Timer;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

import static de.uniks.pioneers.GameConstants.*;


public class BuildingPointController {
    private final Pane fieldPane;
    private final Circle view;
    private final Circle eventView;
    private final IngameService ingameService;
    private final TimerService timerService;

    private final UserService userService;

    private final GameStorage gameStorage;
    private final String gameId;
    private String action;
    public HexTile tile;


    // coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];

    public ArrayList<StreetPointController> adjacentStreets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Building building = null;

    private SVGPath displayedBuilding = null;

    public BuildingPointController(HexTile tile, Circle view,
                                   IngameService ingameService, String gameId,
                                   Pane fieldPane, GameStorage gameStorage,
                                   UserService userService, TimerService timerService) {

        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.timerService = timerService;
        this.userService = userService;
        this.gameStorage = gameStorage;
        this.gameId = gameId;
        this.fieldPane = fieldPane;

        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(15);
        this.eventView.setOpacity(0);
    }

    public void init() {
        this.eventView.setOnMouseClicked(this::info);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }

    public void addEventArea() {this.fieldPane.getChildren().add(eventView);}

    public Circle getView() {
        return this.view;
    }

    public HexTile getTile() {
        return this.tile;
    }

    public ArrayList<StreetPointController> getAdjacentStreets() {
        return this.adjacentStreets;
    }

    public void build() {
        // post build move
        System.out.println("Action: " + this.action);
        String buildingType;
        if (this.action.contains("settlement")) {
            buildingType = "settlement";
        } else {
            buildingType = gameStorage.selectedBuilding;
        }

        System.out.println("SENDING " + buildingType);
        CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], buildingType);
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    if (move.action().equals(BUILD)) {
                        timerService.reset();
                    }
                    this.fieldPane.getChildren().forEach(this::reset);
                }));

    }
    private void reset(Node node) {
        node.setOnMouseClicked(null);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    public void placeBuilding(Building building) {
        // create new svg
        SVGPath buildingSVG = new SVGPath();
        System.out.println("placing: " + building.type());
        if(building.type().equals(SETTLEMENT)){
            buildingSVG.setContent(GameConstants.SETTLEMENT_SVG);}
        else{
            buildingSVG.setContent(CITY_SVG);}
        buildingSVG.setFill(Color.WHITE);
        buildingSVG.setStrokeWidth(1.5);
        buildingSVG.setStrokeType(StrokeType.OUTSIDE);

        // set color of building
        disposable.add(ingameService.getPlayer(building.gameId(), building.owner())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> buildingSVG.setStroke(Paint.valueOf(player.color()))));

        // set position on game field
        buildingSVG.setLayoutX(view.getLayoutX() - GameConstants.HOUSE_WIDTH / 1.2);
        buildingSVG.setLayoutY(view.getLayoutY() - GameConstants.HOUSE_HEIGHT);
        this.fieldPane.getChildren().remove(this.displayedBuilding);
        this.fieldPane.getChildren().add(buildingSVG);

        // set building of this controller
        this.building = building;
        this.displayedBuilding = buildingSVG;
        this.view.toFront();
        this.view.setVisible(false);
        this.eventView.toFront();

    }

    private void info(MouseEvent mouseEvent) {
        boolean invalid = false;
        if(gameStorage.selectedBuilding.equals(SETTLEMENT) || gameStorage.selectedBuilding.equals("")) {
            for (StreetPointController street : adjacentStreets) {
                for (BuildingPointController building : street.getAdjacentBuildings()) {
                    if (building != this) {
                        if (building.building != null) {
                            invalid = true;
                        }
                    }
                }
            }
        }
        if(gameStorage.selectedBuilding.equals(CITY)) {
            if(this.building == null || !this.building.type().equals(SETTLEMENT) || !this.building.owner().equals(this.userService.getCurrentUser()._id())){

                invalid = true;
            }

        }
        if (invalid) {
            System.out.println("You can't build here!");
        } else {
            build();
        }
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(GREEN);
        this.view.setVisible(true);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(RED);
        if(this.building != null){
        this.view.setVisible(false);}
    }

    public void mark() {
        this.view.setFill(BLUE);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String generateKeyString() {
        return uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3];
    }

    public Building getBuilding() {
        return building;
    }
}