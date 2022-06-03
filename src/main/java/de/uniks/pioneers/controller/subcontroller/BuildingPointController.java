package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class BuildingPointController {
    private Pane fieldpane;
    private Circle view;
    private final IngameService ingameService;
    private final String gameId;
    private String action;
    public HexTile tile;

    public ArrayList<StreetPointController> streets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    public BuildingPointController(HexTile tile, Circle view,
                                   IngameService ingameService, String gameId,
                                   Pane fieldpane){

        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.gameId = gameId;
        this.fieldpane = fieldpane;
    }

    public void init(){
        this.view.setOnMouseClicked(this::build);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);

    }

    public void reset() {
        this.view.setOnMouseClicked(null);
        this.view.setOnMouseEntered(null);
        this.view.setOnMouseExited(null);
    }

    private void build(MouseEvent mouseEvent){
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
        CreateBuildingDto newBuilding = new CreateBuildingDto(tile.q, tile.r, tile.s, 6, buildingType);
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    System.out.println(move);
                    this.reset();
                }));

    }

    public void showBuilding(Building building) {
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

        System.out.println("Placed on: " + svgShape.getLayoutX() + " " + svgShape.getLayoutY());
    }

    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}
    public void mark(){this.view.setFill(Color.rgb(0,0,255));}

    public void setAction(String action) {
        this.action = action;
    }
}