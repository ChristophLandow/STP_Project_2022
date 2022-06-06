package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class StreetPointController {

    private final Circle view;
    public HexTile tile;

    //coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];

    public ArrayList<BuildingPointController> buildings = new ArrayList<>();

    public StreetPointController(HexTile tile, Circle view){

        this.tile = tile;
        this.view = view;
        // init();
    }

    public void init(){
        this.view.setOnMouseClicked(this::info);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);

    }
    public void build(){
        //assigns building
    }

    public ArrayList<BuildingPointController> getBuildings(){
        return this.buildings;
    }

    private void info(MouseEvent mouseEvent){

        for(BuildingPointController buildingPointController : this.buildings){
            buildingPointController.mark();
        }

        System.out.println(tile);
    }
    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}

    public void mark(){this.view.setFill(Color.rgb(0,0,255));}
}
