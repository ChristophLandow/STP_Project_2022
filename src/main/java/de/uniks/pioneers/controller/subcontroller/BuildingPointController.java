package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import static de.uniks.pioneers.GameConstants.RED;

public class BuildingPointController {
    private final Circle view;
    public HexTile tile;

    public ArrayList<StreetPointController> streets = new ArrayList<>();

    public BuildingPointController(HexTile tile, Circle view){

        this.tile = tile;
        this.view = view;
        init();
    }

    public void init(){
        this.view.setOnMouseClicked(this::info);
        //this.view.setOnMouseEntered(this::dye);
        //this.view.setOnMouseExited(this::undye);

    }

    public Circle getView(){
        return this.view;
    }

    public HexTile getTile(){
        return this.tile;
    }

    public void build(){
        mark();
    }

    public ArrayList<StreetPointController> getStreets(){
        return this.streets;
    }

    private void info(MouseEvent mouseEvent){
        boolean surroundet = false;
        for(StreetPointController street : streets){
            for(BuildingPointController building : street.getBuildings()){
                if(building != this) {
                    if(building.getView().getFill() != RED){
                        surroundet = true;
                    }
                }
            }
        }
        if(surroundet){
            System.out.println("You can't build here!");
        } else {
            build();
        }
    }

    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}
    public void mark(){this.view.setFill(Color.rgb(0,0,255));}

}