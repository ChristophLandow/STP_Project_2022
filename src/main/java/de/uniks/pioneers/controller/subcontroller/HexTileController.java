package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class HexTileController {
    private final Polygon view;
    private final ImageView numberImage;
    public HexTile tile;
    public BuildingPointController[] corners = new BuildingPointController[6];
    public StreetPointController[] edges = new StreetPointController[6];

    public HexTileController(HexTile tile, Polygon view, ImageView numberImage) {
        this.tile = tile;
        this.view = view;
        this.numberImage = numberImage;
    }

    public void findCorners(ArrayList<BuildingPointController> buildingPointControllers) {
        double[][] cornerCoords = new double[6][2];
        cornerCoords[0] = new double[]{tile.x + 0, tile.y + 1 * tile.scale};
        cornerCoords[1] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  + 0.5 * tile.scale};
        cornerCoords[2] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  - 0.5 * tile.scale};
        cornerCoords[3] = new double[]{tile.x - 0, tile.y - 1 * tile.scale};
        cornerCoords[4] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  - 0.5 * tile.scale};
        cornerCoords[5] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  + 0.5 * tile.scale};

        for(int i = 0; i < 6; i++) {
            for(BuildingPointController buildingPoint : buildingPointControllers) {
                if(abs(buildingPoint.tile.x - cornerCoords[i][0]) < 1 && abs(buildingPoint.tile.y - cornerCoords[i][1]) < 1 ) {
                    switch (i) {
                        case 0 -> {
                            buildingPoint.uploadCoords[0] = tile.q;
                            buildingPoint.uploadCoords[2] = tile.r;
                            buildingPoint.uploadCoords[1] = tile.s;
                            buildingPoint.uploadCoords[3] = 0;
                        }
                        case 1 -> {
                            buildingPoint.uploadCoords[0] = tile.q + 1;
                            buildingPoint.uploadCoords[2] = tile.r - 1;
                            buildingPoint.uploadCoords[1] = tile.s;
                            buildingPoint.uploadCoords[3] = 6;
                        }
                        case 2 -> {
                            buildingPoint.uploadCoords[0] = tile.q;
                            buildingPoint.uploadCoords[2] = tile.r + 1;
                            buildingPoint.uploadCoords[1] = tile.s - 1;
                            buildingPoint.uploadCoords[3] = 0;
                        }
                        case 3 -> {
                            buildingPoint.uploadCoords[0] = tile.q;
                            buildingPoint.uploadCoords[2] = tile.r;
                            buildingPoint.uploadCoords[1] = tile.s;
                            buildingPoint.uploadCoords[3] = 6;
                        }
                        case 4 -> {
                            buildingPoint.uploadCoords[0] = tile.q - 1;
                            buildingPoint.uploadCoords[2] = tile.r + 1;
                            buildingPoint.uploadCoords[1] = tile.s;
                            buildingPoint.uploadCoords[3] = 0;
                        }
                        case 5 -> {
                            buildingPoint.uploadCoords[0] = tile.q;
                            buildingPoint.uploadCoords[2] = tile.r - 1;
                            buildingPoint.uploadCoords[1] = tile.s + 1;
                            buildingPoint.uploadCoords[3] = 6;
                        }
                    }

                    this.corners[i] = buildingPoint;
                }
            }
        }
    }

    public void findEdges(ArrayList<StreetPointController> streetPointControllers) {
        double[][] edgeCoords = new double[6][2];
        edgeCoords[0] = new double[]{tile.x + (sqrt(3)/4) * tile.scale, tile.y + 0.75 * tile.scale};
        edgeCoords[1] = new double[]{tile.x + (sqrt(3)/2) * tile.scale, tile.y  + 0};
        edgeCoords[2] = new double[]{tile.x + (sqrt(3)/4) * tile.scale, tile.y - 0.75 * tile.scale};
        edgeCoords[3] = new double[]{tile.x - (sqrt(3)/4) * tile.scale, tile.y - 0.75 * tile.scale};
        edgeCoords[4] = new double[]{tile.x - (sqrt(3)/2) * tile.scale, tile.y  + 0};
        edgeCoords[5] = new double[]{tile.x - (sqrt(3)/4) * tile.scale, tile.y + 0.75 * tile.scale};

        for(int i = 0; i < 6; i++) {
            for(StreetPointController streetPoint : streetPointControllers) {
                if(abs(streetPoint.tile.x - edgeCoords[i][0]) < 1 && abs(streetPoint.tile.y - edgeCoords[i][1]) < 1 ) {

                    switch (i) {
                        case 0 -> {
                            streetPoint.uploadCoords[0] = tile.q + 1;
                            streetPoint.uploadCoords[2] = tile.r - 1;
                            streetPoint.uploadCoords[1] = tile.s;
                            streetPoint.uploadCoords[3] = 7;
                        }
                        case 1 -> {
                            streetPoint.uploadCoords[0] = tile.q;
                            streetPoint.uploadCoords[2] = tile.r;
                            streetPoint.uploadCoords[1] = tile.s;
                            streetPoint.uploadCoords[3] = 3;
                        }
                        case 2 -> {
                            streetPoint.uploadCoords[0] = tile.q;
                            streetPoint.uploadCoords[2] = tile.r + 1;
                            streetPoint.uploadCoords[1] = tile.s - 1;
                            streetPoint.uploadCoords[3] = 11;
                        }
                        case 3 -> {
                            streetPoint.uploadCoords[0] = tile.q;
                            streetPoint.uploadCoords[2] = tile.r;
                            streetPoint.uploadCoords[1] = tile.s;
                            streetPoint.uploadCoords[3] = 7;
                        }
                        case 4 -> {
                            streetPoint.uploadCoords[0] = tile.q - 1;
                            streetPoint.uploadCoords[2] = tile.r;
                            streetPoint.uploadCoords[1] = tile.s + 1;
                            streetPoint.uploadCoords[3] = 3;
                        }
                        case 5 -> {
                            streetPoint.uploadCoords[0] = tile.q;
                            streetPoint.uploadCoords[2] = tile.r;
                            streetPoint.uploadCoords[1] = tile.s;
                            streetPoint.uploadCoords[3] = 11;
                        }
                    }

                    this.edges[i] = streetPoint;
                }
            }
        }
    }

    // interconnects street and building controllers
    public void link(){
        for(int i = 0; i < 6; i++){
            if(!this.corners[i].adjacentStreets.contains(this.edges[i])){
                this.corners[i].adjacentStreets.add(this.edges[i]);
                this.edges[i].adjacentBuildings.add(this.corners[i]);
            }

            if(!this.corners[i].adjacentStreets.contains(this.edges[((i-1)+6)%6])){
                this.corners[i].adjacentStreets.add(this.edges[((i-1)+6)%6]);
                this.edges[((i-1)+6)%6].adjacentBuildings.add(this.corners[i]);
            }
        }
    }

    public void setVisible(boolean isVisible){
        this.view.setVisible(isVisible);

        if(numberImage != null){
            numberImage.setVisible(isVisible);
        }

        for(BuildingPointController buildingPointController: this.corners){
            if(buildingPointController != null) {
                buildingPointController.setVisible(isVisible);
            }
        }

        for(StreetPointController streetPointController: this.edges){
            if(streetPointController != null) {
                streetPointController.setVisible(isVisible);
            }
        }
    }

    public Polygon getView() {
        return view;
    }
}
