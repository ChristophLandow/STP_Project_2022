package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.ResourceService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class StreetPointController {
    private final GameService gameService;
    private final ResourceService resourceService;
    private final IngameService ingameService;

    private final GameStorage gameStorage;
    private Pane fieldPane;
    public HexTile tile;
    private Circle view;
    private Circle eventView;
    private final CompositeDisposable disposable = new CompositeDisposable();
    // coordinates to be uploaded to the server as: x, y, z, side
    public final int[] uploadCoords = new int[4];
    public final ArrayList<BuildingPointController> adjacentBuildings = new ArrayList<>();
    private String action;

    @Inject
    public StreetPointController(GameService gameService, ResourceService resourceService, IngameService ingameService, GameStorage gameStorage) {
        this.gameService = gameService;
        this.resourceService = resourceService;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
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
        checkIfMouseInsideView();

        this.eventView.setOnMouseClicked(this::placeStreet);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
    }

    public void addEventArea() {
        this.eventView.setId(uploadCoords[0] + "," + uploadCoords[1] + "," + uploadCoords[2] + "," + uploadCoords[3]);
        this.eventView.setLayoutX(view.getLayoutX());
        this.eventView.setLayoutY(view.getLayoutY());
        this.fieldPane.getChildren().add(eventView);
    }

    public void placeStreet(MouseEvent mouseEvent) {
        boolean valid;

        if (action.equals(FOUNDING_ROAD_1) || action.equals(FOUNDING_ROAD_2)) {
            valid = checkBuildings();
        } else {
            if (gameStorage.remainingBuildings.get(ROAD) >= 1 && resourceService.checkRoad()) {
                valid = checkRoads() || checkBuildings();
            }else {
                valid = false;
            }
        }

        if (valid) {
            gameStorage.remainingBuildings.put(ROAD, gameStorage.remainingBuildings.get(ROAD)-1 );
            CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], "road");
            disposable.add(ingameService.postMove(gameService.game.get()._id(), new CreateMoveDto(this.action, null, null, null, null, newBuilding))
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

        this.view.setFill(STANDARD_COLOR);
    }

    public void renderRoad(Building building) {
        if(this.view.getRadius() != 0) {
            Player player = gameService.players.get(building.owner());
            double streetWidth = this.gameStorage.getHexScale() / 1.25;
            double streetHeight = this.gameStorage.getHexScale() / 8.3;

            Rectangle street = new Rectangle(view.getLayoutX() - streetWidth/2,view.getLayoutY()- streetHeight/2,streetWidth,streetHeight);
            street.setFill(Paint.valueOf(player.color()));

            if (building.side() == 3) {
                street.setRotate(90);
            } else if (building.side() == 7) {
                street.setRotate(30);
            } else {
                street.setRotate(-30);
            }

            fieldPane.getChildren().add(street);

            this.reset(this.eventView);
            this.view.setVisible(false);
            this.view.setRadius(0);

            for(BuildingPointController bpController:adjacentBuildings){
                if(bpController.displayedBuilding != null){
                    bpController.displayedBuilding.toFront();
                }
            }
        }
    }

    private void checkIfMouseInsideView(){
        //Get Mouse and view position on screen
        Point2D mousePos = new Robot().getMousePosition();

        Bounds viewBounds = view.localToScreen(view.getBoundsInLocal());
        Point2D viewPos =  new Point2D(viewBounds.getMinX() + viewBounds.getWidth()/2, viewBounds.getMinY() + viewBounds.getHeight()/2);

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
