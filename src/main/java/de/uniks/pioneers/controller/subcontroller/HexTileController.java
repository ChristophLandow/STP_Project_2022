package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.RobberService;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Objects;

import static de.uniks.pioneers.GameConstants.HOVER_COLOR;
import static de.uniks.pioneers.GameConstants.STANDARD_COLOR;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class HexTileController {
    private final Pane fieldPane;
    private final Circle eventView;
    private final Circle view;
    private ImageView robber;
    public final HexTile tile;
    public final BuildingPointController[] corners = new BuildingPointController[6];
    public final StreetPointController[] edges = new StreetPointController[6];
    private RobberService robberService;

    public HexTileController(Pane fieldPane, HexTile tile, Circle view, Circle eventView, RobberService robberService) {
        this.fieldPane = fieldPane;
        this.tile = tile;
        this.eventView = eventView;
        this.view = view;
        this.robberService = robberService;
        this.robber = null;

        this.eventView.setOpacity(0);
    }

    public void init(){
        this.eventView.setOnMouseClicked(this::moveRobber);
        this.eventView.setOnMouseEntered(this::dye);
        this.eventView.setOnMouseExited(this::undye);
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
            if(edges[i] != null && corners[i] != null) {
                if (!this.corners[i].adjacentStreets.contains(this.edges[i])) {
                    this.corners[i].adjacentStreets.add(this.edges[i]);
                    this.edges[i].adjacentBuildings.add(this.corners[i]);
                }

                if (!this.corners[i].adjacentStreets.contains(this.edges[((i - 1) + 6) % 6])) {
                    this.corners[i].adjacentStreets.add(this.edges[((i - 1) + 6) % 6]);
                    this.edges[((i - 1) + 6) % 6].adjacentBuildings.add(this.corners[i]);
                }
            }
        }
    }

    private void dye(MouseEvent mouseEvent) {
        if(this.robber == null && robberService != null && this.robberService.getRobberState().get() == GameConstants.ROBBER_MOVE) {
            this.view.setFill(HOVER_COLOR);
            this.view.setVisible(true);
        }
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(STANDARD_COLOR);
        this.view.setVisible(false);
    }

    private void moveRobber(MouseEvent event){
        if(robberService != null && this.robberService.getRobberState().get() == GameConstants.ROBBER_MOVE) {
            this.robberService.moveRobber(this);
            this.robberService.updateRobbingCandidates();
            this.robberService.getRobberState().set(GameConstants.ROBBER_STEAL);
        }
    }

    public void setRobber(boolean placeRobber){
        if(placeRobber && this.robber == null){
            this.robber = new ImageView(new Image(Objects.requireNonNull(getClass().getResource( "steine_3.png")).toString()));
            this.robber.setFitWidth((this.eventView.getRadius()*1.4));
            this.robber.setFitHeight(this.eventView.getRadius()*1.4);
            this.robber.setLayoutX(this.view.getLayoutX() - this.robber.getFitWidth()/2);
            this.robber.setLayoutY(this.view.getLayoutY() - this.robber.getFitHeight()/2);

            this.fieldPane.getChildren().add(this.robber);
            this.eventView.setOpacity(0.9);
        }
        else{
            if(robberService.getRobberTile() != this) {
                this.fieldPane.getChildren().remove(this.robber);
                this.robber = null;
                this.eventView.setOpacity(0);
            }
        }
    }

    public ArrayList<User> getPlayersFromTile(){
        ArrayList<User> result = new ArrayList<>();

        for(BuildingPointController buildingPointController: corners){
            User user = buildingPointController.getOwner();
            if(user != null && !result.contains(user)){
                result.add(user);
            }
        }

        return result;
    }

    public void setVisible(boolean isVisible){
        if(this.robber != null){
            this.robber.setVisible(isVisible);
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

    public Circle getView() {
        return view;
    }

    public Point2D getCenter(){
        return new Point2D(tile.x + this.fieldPane.getPrefWidth()/2, -tile.y + this.fieldPane.getPrefHeight()/2);
    }

    public void setRobberService(RobberService robberService) {
        this.robberService = robberService;
    }
}
