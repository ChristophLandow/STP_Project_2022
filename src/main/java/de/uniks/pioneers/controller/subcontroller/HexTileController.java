package de.uniks.pioneers.controller.subcontroller;


import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class HexTileController {

    private final Polygon view;
    public HexTile tile;

    public BuildingPointController[] corners = new BuildingPointController[6];

    public StreetPointController[] edges = new StreetPointController[6];

    public HexTileController(HexTile tile, Polygon view){

        this.tile = tile;
        this.view = view;
        init();
    }

    public void init(){

        this.view.setOnMouseClicked(this::info);
    }

    public void findCorners(ArrayList<BuildingPointController> buildingPointControllers){

        double[][] cornerCoords = new double[6][2];
        cornerCoords[0] = new double[]{tile.x + 0, tile.y + 1 * tile.scale};
        cornerCoords[1] = new double[]{tile.x - 0, tile.y - 1 * tile.scale};
        cornerCoords[2] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  + 0.5 * tile.scale};
        cornerCoords[3] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  - 0.5 * tile.scale};
        cornerCoords[4] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  + 0.5 * tile.scale};
        cornerCoords[5] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  - 0.5 * tile.scale};

        for(int i = 0; i < 6; i++){

            for(BuildingPointController buildingPoint : buildingPointControllers){

                if(abs(buildingPoint.tile.x - cornerCoords[i][0]) < 1 && abs(buildingPoint.tile.y - cornerCoords[i][1]) < 1 ){

                    this.corners[i] = buildingPoint;
                }
            }
        }
    }

    public void findEdges(ArrayList<StreetPointController> streetPointControllers){

        double[][] edgeCoords = new double[6][2];
        edgeCoords[0] = new double[]{tile.x + (sqrt(3)/4) * tile.scale, tile.y + 0.75 * tile.scale};
        edgeCoords[1] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  + 0};
        edgeCoords[2] = new double[]{tile.x + (sqrt(3)/4) * tile.scale, tile.y - 0.75 * tile.scale};
        edgeCoords[3] = new double[]{tile.x - (sqrt(3)/4) * tile.scale, tile.y - 0.75 * tile.scale};
        edgeCoords[4] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  + 0};
        edgeCoords[5] = new double[]{tile.x - (sqrt(3)/4) * tile.scale, tile.y + 0.75 * tile.scale};

        for(int i = 0; i < 6; i++){

            for(StreetPointController streetPoint : streetPointControllers){

                if(abs(streetPoint.tile.x - edgeCoords[i][0]) < 1 && abs(streetPoint.tile.y - edgeCoords[i][1]) < 1 ){

                    this.edges[i] = streetPoint;
                }
            }
        }
    }

    // interconnects street and building controllers
    public void link(){

        for(int i = 0; i < 6; i++){

            if(!this.corners[i].streets.contains(this.edges[i])){

                this.corners[i].streets.add(this.edges[i]);
                this.edges[i].buildings.add(this.corners[i]);
            }
            if(!this.corners[i].streets.contains(this.edges[((i-1)+6)%6])){

                this.corners[i].streets.add(this.edges[((i-1)+6)%6]);
                this.edges[((i-1)+6)%6].buildings.add(this.corners[i]);
            }
        }
    }

    private void info(MouseEvent mouseEvent){

        this.yield();

        System.out.println(tile);
    }
    public void yield(){

        for(BuildingPointController buildingPointController : this.corners){

            if(buildingPointController != null){
            buildingPointController.mark();}
        }
    }

}
