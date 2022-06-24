package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.MapRenderService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import javax.inject.Inject;

import static de.uniks.pioneers.GameConstants.*;

public class ZoomableScrollPane {

    private ScrollPane scrollPane;
    private AnchorPane anchorPane;
    private Canvas canvas;

    private GraphicsContext gc;

    private Pane fieldPane;

    final private Scale fieldScale = new Scale();

    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;

    private double mapWidth;
    private double mapHeight;

    @Inject
    ZoomableScrollPane(GameStorage gameStorage, MapRenderService mapRenderService){
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
    }

    public void init(ScrollPane scrollPane, AnchorPane anchorPane, Pane fieldPane, Canvas canvas){
        this.scrollPane = scrollPane;
        this.fieldPane = fieldPane;
        this.anchorPane = anchorPane;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        resizeMap();

        this.fieldPane.getTransforms().add(fieldScale);

        this.mapRenderService.setMapCanvas(canvas);
        this.mapRenderService.setGc(gc);
    }

    public void render(){
        if(gameStorage.getZoomedOut() != -1) {
            Platform.runLater(this::zoomOut);
        }

        addMouseScrolling(anchorPane);

        Platform.runLater(mapRenderService::checkPoints);
        this.anchorPane.heightProperty().addListener(observable -> scrollPane.setVvalue(0.5));
        this.anchorPane.widthProperty().addListener(observable -> {
            scrollPane.setHvalue(0.5);
            Platform.runLater(mapRenderService::checkPoints);
        });
    }

    public void zoomIn(){
        if(gameStorage.getZoomedIn() != -1) {
            fieldPane.setLayoutX(0);
            fieldPane.setLayoutY(0);
            zoom(1);
            zoom(gameStorage.getZoomedIn());

            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
    }

    public void zoomOut(){
        if(gameStorage.getZoomedOut() != -1) {
            zoom(1);
            zoom(gameStorage.getZoomedOut());

            double paddingLeft = (MAP_WIDTH - (gameStorage.getZoomedOut()*mapWidth))/2 + 1;

            double paddingTop = 0;
            if(gameStorage.getMapRadius() < 2){
                paddingTop = (MAP_HEIGHT - (gameStorage.getZoomedOut()*mapHeight))/2 + 2;
            }
            fieldPane.setLayoutX(paddingLeft);
            fieldPane.setLayoutY(paddingTop);

            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    public void zoom(double zoomValue){
        //Scales the fieldPane for a zoom effect
        fieldScale.setX(zoomValue);
        fieldScale.setY(zoomValue);
        fieldScale.setPivotX(this.fieldPane.getScaleX());
        fieldScale.setPivotY(this.fieldPane.getScaleY());

        this.anchorPane.setPrefHeight(this.fieldPane.getHeight() * zoomValue);
        this.anchorPane.setPrefWidth(this.fieldPane.getWidth() * zoomValue);
    }

    public void addMouseScrolling(Node node) {
        if(gameStorage.getZoomedIn() != -1) {
            node.setOnScroll((ScrollEvent event) -> {
                if (event.isControlDown() && gameStorage.getZoomedOut() != -1) {
                    double deltaY = event.getDeltaY();
                    if (deltaY < 0) {
                        zoomOut();
                    } else {
                        zoomIn();
                    }
                }

                mapRenderService.checkPoints();
            });
        }

        node.setOnMouseDragged((MouseEvent e) -> mapRenderService.checkPoints());
    }

    private void resizeMap(){
        double hexagonWidth = Math.sqrt(3) * gameStorage.getHexScale();
        mapWidth = (2*gameStorage.getMapRadius() + 1) * hexagonWidth;
        mapWidth += (mapWidth*MAP_PADDING);

        double hexagonHeight = 2 * gameStorage.getHexScale();
        mapHeight = (2*gameStorage.getMapRadius() + 1) * hexagonHeight;
        mapHeight += (mapHeight*MAP_PADDING);

        this.fieldPane.setPrefHeight(mapHeight);
        this.fieldPane.setPrefWidth(mapWidth);

        this.anchorPane.setPrefHeight(mapHeight);
        this.anchorPane.setPrefWidth(mapWidth);

        this.canvas.setHeight(mapHeight);
        this.canvas.setWidth(mapWidth);
    }
}
