package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BuildingPointController {
    private final Circle view;
    public HexTile tile;

    public ArrayList<StreetPointController> streets = new ArrayList<>();

    public BuildingPointController(HexTile tile, Circle view){

        this.tile = tile;
        this.view = view;
        // init();
    }

    public void init(){
        this.view.setOnMouseClicked(this::info);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);

    }

    public void reset() {
        this.view.setOnMouseClicked(null);
        this.view.setOnMouseEntered(null);
        this.view.setOnMouseExited(null);
    }

    public void build(){
        //assigns building
    }
    private void info(MouseEvent mouseEvent){

        for(StreetPointController streetPointController : this.streets){
            streetPointController.mark();
        }

        System.out.println(tile);
    }
    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}
    public void mark(){this.view.setFill(Color.rgb(0,0,255));}

}