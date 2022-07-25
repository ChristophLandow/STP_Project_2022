package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.MapEditorController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.Objects;

public class EditTile {

    public HexTile hexTile;

    Polygon view;

    ImageView numberView;

    MapEditorController mapEditorController;

    public EditTile(HexTile hexTile, Polygon view, ImageView numberView, MapEditorController mapEditorController){

        this.hexTile = hexTile;
        this.view = view;
        this.numberView = numberView;

        this.mapEditorController = mapEditorController;

        init();

    }

    private void init(){

        this.view.setOnMouseClicked(this::place);
    }

    private void place(MouseEvent mouseEvent) {

        System.out.println(this.mapEditorController.selection);

        if(!this.mapEditorController.selection.equals("")) {

            if(this.mapEditorController.selection.endsWith("num")){
                this.hexTile.number = Integer.parseInt(this.mapEditorController.selection.replace("num", ""));
                System.out.println("controller/ingame/" + "tile_" + this.hexTile.number + ".png");
                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "tile_" + this.hexTile.number + ".png")).toString());
                this.numberView.setImage(image);
                this.numberView.toFront();
                return;
            }
            Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.mapEditorController.selection + ".png")).toString());

            this.view.setFill(new ImagePattern(image));
            this.hexTile.type = this.mapEditorController.selection;
        }
    }
}
