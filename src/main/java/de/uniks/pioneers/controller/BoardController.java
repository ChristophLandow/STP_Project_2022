package de.uniks.pioneers.controller;

import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private final IngameService ingameService;
    private final GameService gameService;
    private final UserService userService;
    private final MapRenderService mapRenderService;
    public final SimpleObjectProperty<Game> game;

    private  Thread hextileRenderThread;

    public BoardController(IngameService ingameService, UserService userService, SimpleObjectProperty<Game> game,
                           GameStorage gameStorage, GameService gameService, MapRenderService mapRenderService){
        this.ingameService = ingameService;
        this.gameService = gameService;
        this.userService = userService;
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
        this.game = game;
    }


    public void buildBoardUI() {
        BoardGenerator generator = new BoardGenerator();
        List<HexTile> tiles = generator.generateTiles(this.gameStorage.getMap(), this.gameStorage.getHexScale());
        List<HexTile> edges = generator.generateEdges(2 * gameStorage.getMapRadius() + 1, gameStorage.getHexScale());
        List<HexTile> corners = generator.generateCorners(2 * gameStorage.getMapRadius() + 1, gameStorage.getHexScale());
        List<HexTile> harbors = generator.generateHarbors(this.gameStorage.getHarbors(), this.gameStorage.getHexScale());

        if(gameStorage.getMapRadius() > 4) {
            this.hextileRenderThread = new Thread(() -> {
                try {
                    for (HexTile hexTile : tiles) {
                        Platform.runLater(() -> loadHexagon(hexTile));
                        Thread.sleep(mapRenderService.calcSleepHexagon());
                    }

                    for (HexTile edge : edges) {
                        Platform.runLater(() -> loadEdge(edge));
                        Thread.sleep(0,500000);
                    }

                    for (HexTile corner : corners) {
                        Platform.runLater(() -> loadCorner(corner));
                        Thread.sleep(0,500000);
                    }

                    for (HexTile harbor : harbors) {
                        Platform.runLater(() -> loadHarbor(harbor));
                        Thread.sleep(0,500000);
                    }

                    Thread.sleep(1000);
                    linkTiles();

                    Thread.sleep(10);
                    mapRenderService.setTileControllers(this.tileControllers);
                    loadSnowAnimation();
                    mapRenderService.setFinishedLoading(true);
                }
                catch (InterruptedException ignored){}
            });

            hextileRenderThread.setDaemon(true);
            hextileRenderThread.start();
        }
        else{
            tiles.forEach(this::loadHexagon);
            edges.forEach(this::loadEdge);
            corners.forEach(this::loadCorner);
            harbors.forEach(this::loadHarbor);
            linkTiles();
            mapRenderService.setTileControllers(this.tileControllers);
            loadSnowAnimation();
            mapRenderService.setFinishedLoading(true);
        }

    }

    private void loadHexagon(HexTile hexTile){
        drawCanvasHexagon(
                new double[]{0.0, Math.sqrt(3)/2, Math.sqrt(3)/2, 0.0, -Math.sqrt(3)/2, -Math.sqrt(3)/2},
                new double[]{1.0, 0.5, -0.5, -1.0, -0.5, 0.5},
                hexTile.x + this.fieldPane.getPrefWidth() / 2,
                -hexTile.y + this.fieldPane.getPrefHeight() / 2,
                hexTile.type,
                hexTile.number
        );

        Circle hexView = new Circle(gameStorage.getHexScale()/8);
        hexView.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
        hexView.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2);
        hexView.setVisible(false);
        this.fieldPane.getChildren().add(hexView);

        Circle eventHexView = new Circle(gameStorage.getHexScale()/1.4);
        eventHexView.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
        eventHexView.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2);
        eventHexView.setFill(Color.gray(0,0.1));
        this.fieldPane.getChildren().add(eventHexView);

        HexTileController newHexTileController = new HexTileController(fieldPane, hexTile, hexView, eventHexView);
        this.tileControllers.add(newHexTileController);
    }

    private void loadEdge(HexTile edge){
        Circle circ = new Circle(gameStorage.getHexScale() / 16.5);
        circ.setFill(STANDARD_COLOR);

        circ.setLayoutX(edge.x + this.fieldPane.getPrefWidth() / 2);
        circ.setLayoutY(-edge.y + this.fieldPane.getPrefHeight() / 2);
        this.fieldPane.getChildren().add(circ);
        StreetPointController streetPointController = streetPointControllerProvider.get();
        streetPointController.post(edge, circ, this.fieldPane);
        streetPointControllers.add(streetPointController);
    }

    private void loadCorner(HexTile corner){
        Circle circ = new Circle(gameStorage.getHexScale() / 12.5);
        circ.setFill(STANDARD_COLOR);

        circ.setLayoutX(corner.x + this.fieldPane.getPrefWidth() / 2);
        circ.setLayoutY(-corner.y + this.fieldPane.getPrefHeight() / 2);
        this.fieldPane.getChildren().add(circ);
        BuildingPointController newbuildingPointController = new BuildingPointController(corner, circ, ingameService, this.gameService, game.get()._id(), this.fieldPane, this.gameStorage, this.userService);
        this.buildingControllers.add(newbuildingPointController);
    }

    private void loadHarbor(HexTile harbor) {
        ImageView imageV = getHarborImage(harbor.type);
        this.fieldPane.getChildren().add(placeHarbor(harbor.x, harbor.y, imageV, harbor.number));
    }

    private void linkTiles(){
        for (HexTileController tile : this.tileControllers) {
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

        mapRenderService.setTileControllers(this.tileControllers);
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

            if(!controller.alreadyPlacedStreet()) {
                controller.renderRoad(building);
            }
        }
    }

    public void enableHexagonPoints(){
        for (HexTileController controller : tileControllers) {
            controller.init();
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

    public void drawCanvasHexagon(double[] xPoints, double[] yPoints, double layoutX, double layoutY, String type, int number){
        //Render Hex Tile image
        //See "Size and Spacing" in the Hexagonal Grids Doku
        double hexagonWidth = Math.sqrt(3) * gameStorage.getHexScale();
        double hexagonHeight = 2 * gameStorage.getHexScale();
        Image image = new Image(Objects.requireNonNull(getClass().getResource("ingame/" + type + ".png")).toString());

        mapRenderService.getGc().drawImage(image, layoutX - hexagonWidth/2, layoutY - hexagonHeight/2, hexagonWidth, hexagonHeight);

        //Render number image
        if(!type.equals("desert")) {
            String numberURL = "ingame/tile_" + number + ".png";
            Image numberImg = new Image(Objects.requireNonNull(getClass().getResource(numberURL)).toString());

            mapRenderService.getGc().drawImage(numberImg, layoutX - gameStorage.getHexScale() / 2.5 / 2, layoutY - gameStorage.getHexScale() / 2.5 / 2, gameStorage.getHexScale() / 2.5, gameStorage.getHexScale() / 2.5);
        }

        //Render Hex tile borders
        //Calculate new points depending on the canvas position and scale. More infos:
        //https://stackoverflow.com/questions/31125511/scale-polygons-by-a-ratio-using-only-a-list-of-their-vertices
        for(int i = 0; i < 6; i++){
            //First convert point to point on canvas
            xPoints[i] += layoutX;
            yPoints[i] += layoutY;

            //Scale hexagon
            xPoints[i] = (gameStorage.getHexScale() * (xPoints[i] - layoutX)) + layoutX;
            yPoints[i] = (gameStorage.getHexScale() * (yPoints[i] - layoutY)) + layoutY;
        }

        mapRenderService.getGc().setStroke(Color.BLACK);
        mapRenderService.getGc().strokePolygon(xPoints, yPoints, 6);
    }

    private ImageView getHarborImage(String type) {
        if (type == null) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_general.png")).toString());
        } else if (type.equals("ore")) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_coal.png")).toString());
        } else if (type.equals("brick")) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_iceberg.png")).toString());
        } else if (type.equals("wool")) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_polar-bear.png")).toString());
        } else if (type.equals("lumber")) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_fish.png")).toString());
        } else if (type.equals("grain")) {
            return new ImageView(Objects.requireNonNull(getClass().getResource("ingame/harbour_whale.png")).toString());
        } else {
            return null;
        }
    }

    private ImageView placeHarbor(double x, double y, ImageView image, Integer side) {
        double width = this.fieldPane.getPrefWidth();
        double height = this.fieldPane.getPrefHeight();
        double scale = this.gameStorage.getHexScale();
        double x_plus = x + width / 2 - scale / 2 + 0.75 * scale;
        double x_minus = x + width / 2 - scale / 2 - 0.75 * scale;
        double y_plus = -y + height / 2 - scale / 2 + 1.25 * scale;
        double y_minus = -y + height / 2 - scale / 2 - 1.25 * scale;
        if (side == 1) {
            image.setLayoutX(x_plus);
            image.setLayoutY(y_minus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(30);
        } else if (side == 3) {
            image.setLayoutX(x + width / 2 - scale / 2 + 1.5 * scale);
            image.setLayoutY(-y + height / 2 - scale / 2);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(90);
        } else if (side == 5) {
            image.setLayoutX(x_plus);
            image.setLayoutY(y_plus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(150);
        } else if (side == 7) {
            image.setLayoutX(x_minus);
            image.setLayoutY(y_plus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(210);
        } else if (side == 9) {
            image.setLayoutX(x + width / 2 - scale / 2 - 1.5 * scale);
            image.setLayoutY(-y + height / 2 - scale / 2);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(270);
        } else if (side == 11) {
            image.setLayoutX(x_minus);
            image.setLayoutY(y_minus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(330);
        }
        return image;
    }

    public void stop() {
        if(hextileRenderThread != null) this.hextileRenderThread.interrupt();
        this.buildingControllers.clear();
        this.streetPointControllers.clear();
        this.tileControllers.clear();
    }
}
