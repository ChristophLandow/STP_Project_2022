package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.TimerService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

import static de.uniks.pioneers.GameConstants.*;


public class BuildingPointController {
    private final Pane fieldPane;
    private final Circle view;
    private final Circle eventView;
    private final IngameService ingameService;
    private final TimerService timerService;
    private final String gameId;
    private String action;
    public HexTile tile;


    // coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];

    public ArrayList<StreetPointController> adjacentStreets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Building building = null;

    public BuildingPointController(HexTile tile, Circle view,
                                   IngameService ingameService, TimerService timerService,
                                   String gameId, Pane fieldPane) {

        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.timerService = timerService;
        this.gameId = gameId;
        this.fieldPane = fieldPane;

        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(15);
        this.eventView.setOpacity(0);
    }

    public void init() {
        if (this.action.equals(BUILD)) {
            // TODO: set builder timer, in progress...
            // this.timerService.setBuildTimer();
        }
        this.eventView.setOnMouseClicked(this::info);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }

    public void addEventArea() {
        this.fieldPane.getChildren().add(eventView);
    }

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
        String buildingType;
        if (this.action.contains("settlement")) {
            buildingType = "settlement";
        } else {
            buildingType = "city";
        }

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
        // create new settlement svg
        SVGPath settlementSVG = new SVGPath();
        settlementSVG.setContent(GameConstants.SETTLEMENT_SVG);
        settlementSVG.setFill(Color.WHITE);
        settlementSVG.setStrokeWidth(1.5);
        settlementSVG.setStrokeType(StrokeType.OUTSIDE);

        // set color of building
        disposable.add(ingameService.getPlayer(building.gameId(), building.owner())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> settlementSVG.setStroke(Paint.valueOf(player.color()))));

        // set position on game field
        settlementSVG.setLayoutX(view.getLayoutX() - GameConstants.HOUSE_WIDTH / 1.2);
        settlementSVG.setLayoutY(view.getLayoutY() - GameConstants.HOUSE_HEIGHT);
        this.fieldPane.getChildren().add(settlementSVG);

        // set building of this controller
        this.building = building;

    }

    private void info(MouseEvent mouseEvent) {
        boolean surrounded = false;
        for (StreetPointController street : adjacentStreets) {
            for (BuildingPointController building : street.getAdjacentBuildings()) {
                if (building != this) {
                    if (building.building != null) {
                        surrounded = true;
                    }
                }
            }
        }
        if (surrounded) {
            System.out.println("You can't build here!");
        } else {
            build();
        }
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(GREEN);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(RED);
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