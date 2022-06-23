package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.MapRenderService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import javax.inject.Inject;

public class ZoomableScrollPane {

    private ScrollPane scrollPane;
    private AnchorPane anchorPane;
    private Pane fieldPane;
    private Label loadingLabel;

    final private Scale fieldScale = new Scale();

    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;

    @Inject
    ZoomableScrollPane(GameStorage gameStorage, MapRenderService mapRenderService){
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
    }

    public void init(ScrollPane scrollPane, Pane fieldPane, AnchorPane anchorPane, Label loadingLabel){
        this.scrollPane = scrollPane;
        this.fieldPane = fieldPane;
        this.anchorPane = anchorPane;
        this.loadingLabel = loadingLabel;

        this.fieldPane.getTransforms().add(fieldScale);

        if(gameStorage.getZoomedOut() != -1) {
            zoomOut();
        }
        else{
            zoomIn();
        }

        loadingLabel.setVisible(false);

        addMouseScrolling(anchorPane);

        Platform.runLater(mapRenderService::checkPoints);
        this.anchorPane.heightProperty().addListener(observable -> scrollPane.setVvalue(0.5));
        this.anchorPane.widthProperty().addListener(observable -> {
            scrollPane.setHvalue(0.5);
            Platform.runLater(mapRenderService::checkPoints);
        });
    }

    public void zoomIn(){
        if(gameStorage.getZoomedIn() != gameStorage.getZoomedOut()) {
            zoom(1);
            zoom(gameStorage.getZoomedIn());
            scrollPane.setFitToHeight(false);
            scrollPane.setFitToWidth(false);
        }
    }

    public void zoomOut(){
        if(gameStorage.getZoomedOut() != -1) {
            zoom(1);
            zoom(gameStorage.getZoomedOut());

            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
        }
    }

    public void zoom(double zoomValue){
        //Scales the fieldPane for a zoom effect
        fieldScale.setX(zoomValue);
        fieldScale.setY(zoomValue);
        fieldScale.setPivotX(this.fieldPane.getScaleX());
        fieldScale.setPivotY(this.fieldPane.getScaleY());

        this.anchorPane.setPrefHeight(this.fieldPane.getHeight()*zoomValue);
        this.anchorPane.setPrefWidth(this.fieldPane.getWidth()*zoomValue);
    }

    public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            if(event.isControlDown() && gameStorage.getZoomedOut() != -1) {
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomOut();
                } else{
                    zoomIn();
                }
            }

            mapRenderService.checkPoints();
        });

        node.setOnMouseDragged((MouseEvent e) -> mapRenderService.checkPoints());
    }
}
