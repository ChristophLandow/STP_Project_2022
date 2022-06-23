package de.uniks.pioneers.controller;

import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import static de.uniks.pioneers.GameConstants.*;
import static de.uniks.pioneers.GameConstants.CITY;

public class BoardController {


    public Pane fieldPane;

    public Provider<StreetPointController> streetPointControllerProvider;

    private final GameStorage gameStorage;
    private final ArrayList<BuildingPointController> buildingControllers = new ArrayList<>();
    private final HashMap<String, BuildingPointController> buildingPointControllerHashMap = new HashMap<>();
    private final HashMap<String, StreetPointController> streetPointControllerHashMap = new HashMap<>();
    private final ArrayList<StreetPointController> streetPointControllers = new ArrayList<>();
    public final ArrayList<HexTileController> tileControllers = new ArrayList<>();
    private final int gameSize;
    private final IngameService ingameService;
    private final UserService userService;
    private final TimerService timerService;
    public SimpleObjectProperty<Game> game;

    public BoardController(IngameService ingameService, UserService userService, TimerService timerService, SimpleObjectProperty<Game> game, int gameSize, GameStorage gameStorage){

        this.ingameService = ingameService;
        this.userService = userService;
        this.timerService = timerService;
        this.game = game;
        this.gameSize = gameSize;
        this.gameStorage = gameStorage;

    }


    public void buildBoardUI() {
        BoardGenerator generator = new BoardGenerator();
        List<HexTile> tiles = generator.generateTiles(this.gameStorage.getMap(), this.gameStorage.getHexScale());
        List<HexTile> edges = generator.generateEdges(2 * this.gameSize + 1, gameStorage.getHexScale());
        List<HexTile> corners = generator.generateCorners(2 * this.gameSize + 1, gameStorage.getHexScale());

        for (HexTile hexTile : tiles) {

            Polygon hex = new Polygon();
            hex.getPoints().addAll(
                    0.0, 1.0,
                    Math.sqrt(3) / 2, 0.5,
                    Math.sqrt(3) / 2, -0.5,
                    0.0, -1.0,
                    -Math.sqrt(3) / 2, -0.5,
                    -Math.sqrt(3) / 2, 0.5);
            hex.setScaleX(gameStorage.getHexScale());
            hex.setScaleY(gameStorage.getHexScale());
            Image image = new Image(Objects.requireNonNull(getClass().getResource("ingame/" + hexTile.type + ".png")).toString());
            hex.setFill(new ImagePattern(image));
            hex.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
            hex.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(hex);

            if (!hexTile.type.equals("desert")) {
                String numberURL = "ingame/tile_" + hexTile.number + ".png";
                ImageView numberImage = new ImageView(Objects.requireNonNull(getClass().getResource(numberURL)).toString());
                double numberSize = gameStorage.getHexScale()/2.5;
                numberImage.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2 - numberSize/2);
                numberImage.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2 - numberSize/2);
                numberImage.setFitHeight(numberSize);
                numberImage.setFitWidth(numberSize);
                this.fieldPane.getChildren().add(numberImage);
            }
            this.tileControllers.add(new HexTileController(hexTile, hex));
        }

        for (HexTile edge : edges) {

            Circle circ = new Circle(gameStorage.getHexScale()/16.5);
            circ.setFill(BUILDING_POINT_STANDARD);

            circ.setLayoutX(edge.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(-edge.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            StreetPointController streetPointController = streetPointControllerProvider.get();
            streetPointController.post(edge, circ, this.fieldPane);
            streetPointControllers.add(streetPointController);
        }

        for (HexTile corner : corners) {

            Circle circ = new Circle(gameStorage.getHexScale()/12.5);
            circ.setFill(BUILDING_POINT_STANDARD);

            circ.setLayoutX(corner.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(-corner.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            this.buildingControllers.add(new BuildingPointController(corner, circ, ingameService, game.get()._id(), this.fieldPane, this.gameStorage, this.userService));
        }
        for (HexTileController tile : tileControllers) {

            tile.findEdges(this.streetPointControllers);
            tile.findCorners(this.buildingControllers);
            tile.link();
        }
        for (BuildingPointController buildingPoint : this.buildingControllers) {
            // put buildingPointControllers in Hashmap to access with coordinates
            this.buildingPointControllerHashMap.put(
                    buildingPoint.generateKeyString(),
                    buildingPoint);
        }
        for (StreetPointController streetPoint : this.streetPointControllers) {
            // put buildingPointControllers in Hashmap to access with coordinates
            this.streetPointControllerHashMap.put(
                    streetPoint.generateKeyString(),
                    streetPoint);
        }
        loadSnowAnimation();
    }

    private void loadSnowAnimation() {new SnowAnimationControllor(fieldPane, buildingControllers, streetPointControllers);}

    public void renderBuilding(Building building) {

        String coords = building.x() + " " + building.y() + " " + building.z() + " " + building.side();
        if (Objects.equals(building.type(), SETTLEMENT) || Objects.equals(building.type(), CITY)) {
            // find corresponding buildingPointController
            BuildingPointController controller = buildingPointControllerHashMap.get(coords);
            controller.placeBuilding(building);
        } else {
            // find corresponding streetPointController
            StreetPointController controller = streetPointControllerHashMap.get(coords);
            controller.renderRoad(building);
        }
    }

    public void enableStreetPoints(String action) {
        for (StreetPointController controller : streetPointControllerHashMap.values()) {
            controller.setAction(action);
            controller.init();
        }
    }
    public void enableBuildingPoints(String action) {
        for (BuildingPointController controller : buildingPointControllerHashMap.values()) {
            controller.setAction(action);
            controller.init();
        }
    }
}
