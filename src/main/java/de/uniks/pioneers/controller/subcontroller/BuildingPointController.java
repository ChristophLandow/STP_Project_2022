package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.BoardController;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
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
import java.util.ArrayList;
import java.util.Arrays;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class BuildingPointController {
    private final Pane fieldPane;
    private final Circle view;
    private final Circle eventView;
    private final IngameService ingameService;
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
                                   UserService userService) {
        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.userService = userService;
        this.gameStorage = gameStorage;
        this.gameId = gameId;
        this.fieldPane = fieldPane;
        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(gameStorage.getHexScale()/5);
        this.eventView.setOpacity(0);
    }

    public void init() {
        this.eventView.setOnMouseClicked(this::info);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }
    public void addEventArea() {this.fieldPane.getChildren().add(eventView);}
    public HexTile getTile() {return this.tile;}

    public void build() {
        // post build move

        String buildingType;
        if (this.action.contains("settlement")) {
            buildingType = "settlement";
        } else {
            buildingType = gameStorage.selectedBuilding;
        }

        CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], buildingType);
        checkTradeOptions();
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, null, null, null, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> this.fieldPane.getChildren().forEach(this::reset)));
    }

    private void reset(Node node) {
        node.setOnMouseClicked(null);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    public void placeBuilding(Building building) {
        // create new svg
        SVGPath buildingSVG = new SVGPath();
        if(building.type().equals(SETTLEMENT)){
            buildingSVG.setContent(GameConstants.SETTLEMENT_SVG);
            buildingSVG.setLayoutX(view.getLayoutX() - HOUSE_WIDTH);
            buildingSVG.setLayoutY(view.getLayoutY() - HOUSE_HEIGHT);
        }
        else{
            buildingSVG.setContent(CITY_SVG);
            buildingSVG.setLayoutX(view.getLayoutX() - CITY_WIDTH);
            buildingSVG.setLayoutY(view.getLayoutY() - CITY_HEIGHT);
        }
        buildingSVG.setFill(Color.WHITE);
        buildingSVG.setStrokeWidth(1.5);
        buildingSVG.setStrokeType(StrokeType.OUTSIDE);

        // set color of building
        disposable.add(ingameService.getPlayer(building.gameId(), building.owner())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> buildingSVG.setStroke(Paint.valueOf(player.color()))));

        buildingSVG.setScaleX(gameStorage.getHexScale()/BUILDING_SCALING);
        buildingSVG.setScaleY(gameStorage.getHexScale()/BUILDING_SCALING);

        // set position on game field
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
        if(gameStorage.remainingBuildings.get(SETTLEMENT) > 0 && gameStorage.selectedBuilding.equals(SETTLEMENT) || gameStorage.selectedBuilding.equals("")) {
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
        if(gameStorage.remainingBuildings.get(CITY) > 0 && gameStorage.selectedBuilding.equals(CITY)) {
            if(this.building == null || !this.building.type().equals(SETTLEMENT) || !this.building.owner().equals(this.userService.getCurrentUser()._id())){

                invalid = true;
            }

        }
        if(!invalid) {

            if(gameStorage.selectedBuilding.equals(SETTLEMENT) || gameStorage.selectedBuilding.equals("")){gameStorage.remainingBuildings.put(SETTLEMENT, gameStorage.remainingBuildings.get(SETTLEMENT) -1);}
            if(gameStorage.selectedBuilding.equals(CITY)){gameStorage.remainingBuildings.put(CITY, gameStorage.remainingBuildings.get(CITY) -1);}
            build();
        }
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(HOVER_COLOR);
        this.view.setVisible(true);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(STANDARD_COLOR);
        if(this.building != null){
            this.view.setVisible(false);}
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

    public void setVisible(boolean isVisible){
        if(this.displayedBuilding != null){
            this.displayedBuilding.setVisible(isVisible);
            this.displayedBuilding.setDisable(!isVisible);
        }
        else{
            this.view.setVisible(isVisible);
            this.view.setDisable(!isVisible);
        }
    }

    private void checkTradeOptions() {
        int upX = 0;
        int upY = 1;
        int upZ = 2;
        for (Harbor harbor : gameStorage.getHarbors()) {
            if (harbor.side() == 1) {
                if (harbor.x() == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && harbor.z() == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if ((harbor.x() + 1) == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && (harbor.z() - 1) == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            } else if (harbor.side() == 3) {
                if ((harbor.x() + 1) == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && (harbor.z() - 1) == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if (harbor.x() == uploadCoords[upX] && (harbor.y() - 1) == uploadCoords[upY] && (harbor.z() + 1) == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            } else if (harbor.side() == 5) {
                if (harbor.x() == uploadCoords[upX] && (harbor.y() - 1) == uploadCoords[upY] && (harbor.z() + 1) == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if (harbor.x() == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && harbor.z() == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            } else if (harbor.side() == 7) {
                if (harbor.x() == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && harbor.z() == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if ((harbor.x() - 1) == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && (harbor.z() + 1) == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            } else if (harbor.side() == 9) {
                if ((harbor.x() - 1) == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && (harbor.z() + 1) == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if (harbor.x() == uploadCoords[upX] && (harbor.y() + 1) == uploadCoords[upY] && (harbor.z() - 1) == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            } else if (harbor.side() == 11) {
                if (harbor.x() == uploadCoords[upX] && (harbor.y() + 1) == uploadCoords[upY] && (harbor.z() - 1) == uploadCoords[upZ] && uploadCoords[3] == 6) {
                    gameStorage.addToTradeOptions(harbor.type());
                } else if (harbor.x() == uploadCoords[upX] && harbor.y() == uploadCoords[upY] && harbor.z() == uploadCoords[upZ] && uploadCoords[3] == 0) {
                    gameStorage.addToTradeOptions(harbor.type());
                }
            }
        }
    }

}