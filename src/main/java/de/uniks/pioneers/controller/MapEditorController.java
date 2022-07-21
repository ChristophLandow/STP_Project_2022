package de.uniks.pioneers.controller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.services.EditorManager;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.MapRenderService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;


public class MapEditorController implements Controller{

    @FXML
    Pane scrollPaneAnchorPane;
    @FXML
    Button buttonToMaps;
    @FXML
    Button buttonSave;

    EditorManager editorManager;



    @Inject
    public MapEditorController(EditorManager editorManager){

        this.editorManager = editorManager;

    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MapEditorScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent parent;
        try {
            parent =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        display(2);

        return parent;
    }

    private void display(int size){

        int scale = 50;

        List<HexTile> frame = this.editorManager.buildFrame(size, scale);

        for(HexTile hexTile : frame){

            Polygon tile = new Polygon();
            tile.getPoints().addAll(0.0*scale, 1.0*scale,
                    (Math.sqrt(3)/2)*scale,0.5*scale,
                    (Math.sqrt(3)/2)*scale,-0.5*scale,
                    0.0*scale,-1.0*scale,
                    (-Math.sqrt(3)/2)*scale,-0.5*scale,
                    (-Math.sqrt(3)/2)*scale,0.5*scale);

            tile.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2);
            tile.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2);

            this.scrollPaneAnchorPane.getChildren().add(tile);
        }


    }

    public void toMaps(){
    }
    public void save(){}


}
