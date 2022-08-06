package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.MapRenderService;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
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
    private Pane fieldPane;
    private InvalidationListener scrollPaneListener;
    final private Scale fieldScale = new Scale();
    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;
    private double mapWidth;
    private double mapHeight;
    private boolean setScrollPaneListener;
    private double map_padding_x, map_padding_y;
    private double map_width, map_height;

    @Inject
    ZoomableScrollPane(GameStorage gameStorage, MapRenderService mapRenderService){
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
    }

    public void init(boolean preview, ScrollPane scrollPane, AnchorPane anchorPane, Pane fieldPane, Canvas canvas){
        this.scrollPane = scrollPane;
        this.fieldPane = fieldPane;
        this.anchorPane = anchorPane;
        this.canvas = canvas;

        if (preview) {
            map_padding_x = 1000;
            map_padding_y = 1000;
            map_width = 373.0;
            map_height = 287.0;
        } else {
            map_padding_x = MAP_PADDING_X;
            map_padding_y = MAP_PADDING_Y;
            map_width = MAP_WIDTH;
            map_height = MAP_HEIGHT;
        }


        resizeMap();

        mapRenderService.isFinishedLoading().addListener(((observable, oldValue, newValue) -> {
            if(newValue && gameStorage.isCustomMap()){
                this.centerMap();
            }
        }));

        this.fieldPane.getTransforms().add(fieldScale);
        this.mapRenderService.setGc(canvas.getGraphicsContext2D());

        setScrollPaneListener = false;
    }

    public void render(){
        if(gameStorage.getZoomedOut() != -1) {
            Platform.runLater(this::zoomOut);
        }

        addMouseScrolling(anchorPane);

        Platform.runLater(mapRenderService::checkPoints);

        this.scrollPaneListener = observable -> {
            scrollPane.setVvalue(0.5);
            scrollPane.setHvalue(0.5);
            Platform.runLater(mapRenderService::checkPoints);
        };
    }

    public void zoomIn(){
        if(gameStorage.getZoomedIn() != -1) {
            fieldPane.setLayoutX(0);
            fieldPane.setLayoutY(0);
            zoom(1);
            zoom(gameStorage.getZoomedIn());

            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setPannable(true);
        }
    }

    public void zoomOut(){
        if(gameStorage.getZoomedOut() != -1) {
            zoom(1);
            zoom(gameStorage.getZoomedOut());

            double paddingLeft = (map_width - (gameStorage.getZoomedOut() * mapWidth)) / 2 + 1;
            double paddingTop = 0;

            if(gameStorage.getMapRadius() < 2){
                paddingTop = (map_height - (gameStorage.getZoomedOut()*mapHeight))/2 + 2;
            }
            else if(gameStorage.isCustomMap()){
                paddingLeft = 0;
            }

            fieldPane.setLayoutX(paddingLeft);
            fieldPane.setLayoutY(paddingTop);

            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPannable(false);
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

                        if(!setScrollPaneListener){
                            setScrollPaneListener = true;
                            this.anchorPane.heightProperty().addListener(scrollPaneListener);
                            this.anchorPane.widthProperty().addListener(scrollPaneListener);
                        }
                    }
                }

                mapRenderService.checkPoints();
            });
        }

        node.setOnMouseDragged((MouseEvent e) -> mapRenderService.checkPoints());
    }

    private void resizeMap() {
        double hexagonWidth = Math.sqrt(3) * gameStorage.getHexScale();
        mapWidth = (2*gameStorage.getMapRadius() + 1) * hexagonWidth + map_padding_x + gameStorage.getHexScale();

        double hexagonHeight = 2 * gameStorage.getHexScale();

        if(gameStorage.getMapRadius()%2 == 0){
            mapHeight = (gameStorage.getMapRadius()+1)*hexagonHeight + gameStorage.getMapRadius()*gameStorage.getHexScale() + map_padding_y + gameStorage.getHexScale();
        }
        else{
            mapHeight = gameStorage.getMapRadius()*hexagonHeight + (gameStorage.getMapRadius()+1)*gameStorage.getHexScale() + gameStorage.getHexScale() + map_padding_y + gameStorage.getHexScale();
        }

        this.fieldPane.setPrefHeight(mapHeight);
        this.fieldPane.setPrefWidth(mapWidth);

        this.anchorPane.setPrefHeight(mapHeight);
        this.anchorPane.setPrefWidth(mapWidth);

        this.canvas.setHeight(mapHeight);
        this.canvas.setWidth(mapWidth);
    }

    private void centerMap() {
        System.out.println("center map");
        //Find out top left and bottom right hex tile position
        Point2D startVal = mapRenderService.getTileControllers().get(0).getCenter();
        Point2D topLeft = startVal;
        Point2D bottomRight = startVal;

        for(HexTileController hexTileController : mapRenderService.getTileControllers()){
            Point2D viewPos = hexTileController.getCenter();

            if(viewPos.getY() < topLeft.getY()){
                topLeft = new Point2D(topLeft.getX(), viewPos.getY());
            }

            if(viewPos.getY() > bottomRight.getY()){
                bottomRight = new Point2D(bottomRight.getX(), viewPos.getY());
            }

            if(viewPos.getX() < topLeft.getX()){
                topLeft = new Point2D(viewPos.getX(), topLeft.getY());
            }

            if(viewPos.getX() > bottomRight.getX()){
                bottomRight = new Point2D(viewPos.getX(), bottomRight.getY());
            }
        }

        //Move fieldPane children to top left corner
        double fieldPaneMoveChildrenX = (fieldPane.getLayoutX() + map_padding_x * 3 + gameStorage.getHexScale()- topLeft.getX());
        double fieldPaneMoveChildrenY = (fieldPane.getLayoutY() + map_padding_y * 3 + gameStorage.getHexScale() - topLeft.getY());

        gameStorage.setFieldPaneMoveChildrenX(fieldPaneMoveChildrenX);
        gameStorage.setFieldPaneMoveChildrenY(fieldPaneMoveChildrenY);

        for(Node n : fieldPane.getChildren()){
            n.setLayoutX(n.getLayoutX() + fieldPaneMoveChildrenX);
            n.setLayoutY(n.getLayoutY() + fieldPaneMoveChildrenY);
        }

        //Reset fieldPane size
        fieldPane.setPrefWidth((bottomRight.getX() - topLeft.getX()) + (map_padding_x * 3 + gameStorage.getHexScale())*2);
        fieldPane.setPrefHeight((bottomRight.getY() - topLeft.getY()) + (map_padding_y * 3 + gameStorage.getHexScale())*2);

        gameStorage.setZoomedOut(Math.min(map_height/fieldPane.getPrefHeight(), map_width/fieldPane.getPrefWidth()));
        zoomOut();
    }
}
