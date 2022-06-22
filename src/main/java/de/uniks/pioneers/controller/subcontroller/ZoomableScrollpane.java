package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameStorage;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import javax.inject.Inject;

public class ZoomableScrollpane {

    private ScrollPane scrollPane;
    private AnchorPane anchorPane;
    private Pane fieldPane;

    final private Scale fieldScale = new Scale();

    private final GameStorage gameStorage;

    @Inject
    ZoomableScrollpane(GameStorage gameStorage){
        this.gameStorage = gameStorage;
    }

    public void init(ScrollPane scrollPane, Pane fieldPane, AnchorPane anchorPane){
        this.scrollPane = scrollPane;
        this.fieldPane = fieldPane;
        this.anchorPane = anchorPane;

        this.fieldPane.getTransforms().add(fieldScale);
        zoom(gameStorage.getZoomedOut());
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        addMouseScrolling(anchorPane);
    }

    public void zoomIn(){
        zoom(1);
        zoom(gameStorage.getZoomedIn());
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);

        this.anchorPane.heightProperty().addListener(observable -> scrollPane.setVvalue(0.5));
        this.anchorPane.widthProperty().addListener(observable -> scrollPane.setHvalue(0.5));
    }

    public void zoomOut(){
        zoom(1);
        zoom(gameStorage.getZoomedOut());
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    public void zoom(double zoomValue){
        fieldScale.setX(zoomValue);
        fieldScale.setY(zoomValue);
        fieldScale.setPivotX(this.fieldPane.getScaleX());
        fieldScale.setPivotY(this.fieldPane.getScaleY());

        this.anchorPane.setPrefHeight(this.fieldPane.getHeight()*zoomValue);
        this.anchorPane.setPrefWidth(this.fieldPane.getWidth()*zoomValue);
    }

    public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            if(event.isControlDown()) {
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomOut();
                } else{
                    zoomIn();
                }
            }
        });
    }
}
