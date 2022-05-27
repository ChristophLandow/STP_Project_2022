package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class BuildingPointController {
    private Circle view;
    public Tile tile;

    public BuildingPointController(Tile tile, Circle view){

        this.tile = tile;
        this.view = view;
        init();
    }

    public void init(){
        this.view.setOnMouseClicked(this::info);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);

    }
    public void build(){
        //assigns building
    }
    private void info(MouseEvent mouseEvent){

        System.out.println(tile);
    }
    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}

}