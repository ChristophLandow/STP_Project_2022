package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

import static de.uniks.pioneers.GameConstants.*;


public class BuildingPointController {
    private Pane fieldpane;
    private Circle view;
    private final IngameService ingameService;
    private final String gameId;
    private String action;
    public HexTile tile;

    //coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];

    public ArrayList<StreetPointController> streets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Building building;

    public BuildingPointController(HexTile tile, Circle view,
                                   IngameService ingameService, String gameId,
                                   Pane fieldpane){

        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.gameId = gameId;
        this.fieldpane = fieldpane;
    }

    public void init() {
        this.view.setOnMouseClicked(this::info);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);
    }

    public Circle getView(){
        return this.view;
    }

    public HexTile getTile(){
        return this.tile;
    }

    public ArrayList<StreetPointController> getStreets(){
        return this.streets;
    }

    public void build() {
        // print info
        for(StreetPointController streetPointController : this.streets){
            streetPointController.mark();
        }
        System.out.println(tile);

        // post build move
        String buildingType;
        if (this.action.contains("settlement")) {
            buildingType = "settlement";
        } else {
            buildingType = "city";
        }
        System.out.println(uploadCoords[0]);
        System.out.println(uploadCoords[1]);
        System.out.println(uploadCoords[2]);
        System.out.println(uploadCoords[3]);
        CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], buildingType);
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    System.out.println(move);
                    this.reset();
                }));

    }

    public void reset() {
        this.view.setOnMouseClicked(null);
        this.view.setOnMouseEntered(null);
        this.view.setOnMouseExited(null);
    }

    public void placeBuilding(Building building) {
        // create new settlement svg
        SVGPath settlementSVG = new SVGPath();
        settlementSVG.setContent(GameConstants.SETTLEMENT_SVG);
        final Region svgShape = new Region();
        svgShape.setShape(settlementSVG);
        svgShape.setMinSize(GameConstants.HOUSE_WIDTH, GameConstants.HOUSE_HEIGHT);
        svgShape.setPrefSize(GameConstants.HOUSE_WIDTH, GameConstants.HOUSE_HEIGHT);
        svgShape.setMaxSize(GameConstants.HOUSE_WIDTH, GameConstants.HOUSE_HEIGHT);
        svgShape.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // set color of building
        disposable.add(ingameService.getPlayer(building.gameId(), building.owner())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> {
                    svgShape.setStyle("-fx-background-color: " + player.color());
                }));

        // set position on game field
        svgShape.setLayoutX(view.getLayoutX() - GameConstants.HOUSE_WIDTH/2);
        svgShape.setLayoutY(view.getLayoutY() - GameConstants.HOUSE_HEIGHT/2);
        this.fieldpane.getChildren().add(svgShape);

        // set building of this controller
        this.building = building;

        System.out.println("Placed on: " + svgShape.getLayoutX() + " " + svgShape.getLayoutY());
    }

    private void info(MouseEvent mouseEvent){
        boolean surrounded = false;
        for(StreetPointController street : streets){
            for(BuildingPointController building : street.getBuildings()){
                if(building != this) {
                    if(building.getView().getFill() != RED){
                        surrounded = true;
                    }
                }
            }
        }
        if(surrounded){
            System.out.println("You can't build here!");
        } else {
            build();
        }
    }
    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(GREEN);
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(RED);
    }

    public void mark() {
        this.view.setFill(BLUE);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String generateKeyString() {
        return uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3];
    }

    public Building getBuilding() {
        return building;
    }
}