package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.MapEditorController;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.Objects;

public class EditTile {

    HexTile hexTile;

    Polygon view;

    MapEditorController mapEditorController;

    public EditTile(HexTile hexTile, Polygon view, MapEditorController mapEditorController){

        this.hexTile = hexTile;
        this.view = view;

        this.mapEditorController = mapEditorController;

        init();

    }

    private void init(){

        this.view.setOnMouseClicked(this::place);
    }

    private void place(MouseEvent mouseEvent) {

        if(!this.mapEditorController.selection.equals("")) {

            Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.mapEditorController.selection + ".png")).toString());

            this.view.setFill(new ImagePattern(image));
        }
    }
}
