package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class BuildingPointController {
    public Pane fieldPane;
    private final GameService gameService;
    private final ResourceService resourceService;
    public Circle view;
    public Circle eventView;
    private final IngameService ingameService;
    private final IngameSelectController ingameSelectController;
    private final UserService userService;
    private final GameStorage gameStorage;
    private final String gameId;
    private String action;
    public final HexTile tile;

    // coordinates to be uploaded to the server as: x, y, z, side
    public final int[] uploadCoords = new int[4];
    public final ArrayList<StreetPointController> adjacentStreets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Building building = null;
    private User owner = null;
    public SVGPath displayedBuilding = null;

    public BuildingPointController(HexTile tile, Circle view,
                                   IngameService ingameService, GameService gameService, String gameId,
                                   Pane fieldPane, GameStorage gameStorage, IngameSelectController ingameSelectController,
                                   UserService userService, ResourceService resourceService) {
        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.ingameSelectController = ingameSelectController;
        this.userService = userService;
        this.gameStorage = gameStorage;
        this.gameId = gameId;
        this.fieldPane = fieldPane;
        this.gameService = gameService;
        this.resourceService = resourceService;
        this.eventView = new Circle();
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.eventView.setRadius(gameStorage.getHexScale() / 5);
        this.eventView.setOpacity(0);
    }

    public void init() {
        checkIfMouseInsideView();
        this.eventView.setOnMouseClicked(this::checkPosition);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }

    public void addEventArea() {
        this.eventView.setId(uploadCoords[0] + "," + uploadCoords[1] + "," + uploadCoords[2] + "," + uploadCoords[3]);
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.fieldPane.getChildren().add(eventView);
    }

    public HexTile getTile() {
        return this.tile;
    }

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
        if(gameId != null) {
            disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, null, null, null, null, newBuilding))
                    .observeOn(FX_SCHEDULER)
                    .subscribe());
        }
    }

    public void reset() {
        this.view.setOnMouseClicked(null);
        this.view.setOnMouseEntered(null);
        this.view.setOnMouseExited(null);

        this.view.setFill(STANDARD_COLOR);
        if (this.building != null) {
            this.view.setVisible(false);
        }
    }

    public void placeBuilding(Building building) {
        // create new svg
        SVGPath buildingSVG = new SVGPath();
        if (building.type().equals(SETTLEMENT)) {
            buildingSVG.setContent(GameConstants.SETTLEMENT_SVG);
            buildingSVG.setLayoutX(view.getLayoutX() - HOUSE_WIDTH);
            buildingSVG.setLayoutY(view.getLayoutY() - HOUSE_HEIGHT);
        } else {
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
                .subscribe(player -> {
                    buildingSVG.setStroke(Paint.valueOf(player.color()));

                    for(User u : gameService.getUsers()){
                        if(u._id().equals(player.userId())){
                            owner = u;
                        }
                    }
                }));

        buildingSVG.setScaleX(gameStorage.getHexScale() / BUILDING_SCALING);
        buildingSVG.setScaleY(gameStorage.getHexScale() / BUILDING_SCALING);

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

    public boolean checkPosition(MouseEvent mouseEvent) {
        boolean validPoint = true;
        int validStreetCount = 0;

        if (action.equals(FOUNDING_SETTLEMENT_1) || action.equals(FOUNDING_SETTLEMENT_2)) {
            for (StreetPointController street : adjacentStreets) {
                validPoint = checkSettlementSpot(validPoint, street);
            }

            if(validPoint) {
                build();
                gameStorage.remainingBuildings.put(SETTLEMENT, gameStorage.remainingBuildings.get(SETTLEMENT) - 1);
            }
        } else {
            if (gameStorage.selectedBuilding.equals(SETTLEMENT)) {
                if (gameStorage.remainingBuildings.get(SETTLEMENT) > 0 && resourceService.checkResourcesSettlement()) {
                    for (StreetPointController street : adjacentStreets) {
                        if (street.alreadyPlacedStreet() && gameService.checkRoadSpot(street.uploadCoords[0], street.uploadCoords[1], street.uploadCoords[2], street.uploadCoords[3])) {
                            validStreetCount += 1;
                            validPoint = checkSettlementSpot(validPoint, street);
                        }
                    }

                    if(validPoint && validStreetCount > 0) {
                        build();
                        gameStorage.remainingBuildings.put(SETTLEMENT, gameStorage.remainingBuildings.get(SETTLEMENT) - 1);
                    }
                }
            } else if (gameStorage.selectedBuilding.equals(CITY)) {
                if (gameStorage.remainingBuildings.get(CITY) > 0 && resourceService.checkCity()) {
                    if (this.building != null || !this.building.type().equals(SETTLEMENT) || !this.building.owner().equals(this.userService.getCurrentUser()._id())) {
                        gameStorage.remainingBuildings.put(CITY, gameStorage.remainingBuildings.get(CITY) - 1);
                        build();
                    }
                }
            }

            ingameSelectController.resetSelect();
        }

        return (validPoint && validStreetCount > 0);
    }

    public boolean checkSettlementSpot(boolean validPoint, StreetPointController street) {
        if (validPoint) {
            for (BuildingPointController building : street.getAdjacentBuildings()) {
                if (building != this) {
                    if (building.building != null) {
                        validPoint = false;
                    }
                }
            }
        }

        return validPoint;
    }

    private void checkIfMouseInsideView(){
        //Get Mouse and view position on screen
        Point2D mousePos = new Robot().getMousePosition();

        Bounds viewBounds = view.localToScreen(view.getBoundsInLocal());
        Point2D viewPos = new Point2D(0,0);
        if(viewBounds != null) {
            viewPos =  new Point2D(viewBounds.getMinX() + viewBounds.getWidth()/2, viewBounds.getMinY() + viewBounds.getHeight()/2);
        }

        //Check if mouse is in eventView
        Point2D deltaPos = new Point2D(Math.abs(mousePos.getX()-viewPos.getX()), Math.abs(mousePos.getY()-viewPos.getY()));
        double viewRadius = gameStorage.getHexScale()/5;
        boolean mouseInView = deltaPos.getX() <= viewRadius && deltaPos.getY() <= viewRadius;

        //If mouse is already in circle dye circle
        if(mouseInView) {
            this.view.setFill(HOVER_COLOR);
            this.view.setVisible(true);
        }
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(HOVER_COLOR);
        this.view.setVisible(true);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(STANDARD_COLOR);
        if (this.building != null) {
            this.view.setVisible(false);
        }
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

    public User getOwner() {
        return owner;
    }

    public void setVisible(boolean isVisible){
        if(this.displayedBuilding != null){
            this.displayedBuilding.setVisible(isVisible);
            this.displayedBuilding.setDisable(!isVisible);
        } else {
            this.view.setVisible(isVisible);
            this.view.setDisable(!isVisible);
        }
    }

    public void checkTradeOptions() {
        // checks for every harbor if it is near the current building point an if so adds the option to tradeOptions in game storage
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