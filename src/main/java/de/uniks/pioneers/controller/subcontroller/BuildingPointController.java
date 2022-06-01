package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class BuildingPointController {
    private Circle view;
    private final IngameService ingameService;
    private final String gameId;
    private String action;
    public HexTile tile;

    public ArrayList<StreetPointController> streets = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    public BuildingPointController(HexTile tile, Circle view, IngameService ingameService, String gameId){

        this.tile = tile;
        this.view = view;
        this.ingameService = ingameService;
        this.gameId = gameId;
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
        CreateBuildingDto newBuilding = new CreateBuildingDto(tile.q, tile.r, tile.r, 6, buildingType);
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    this.showBuilding(move);
                    this.reset();
                }));

    }

    private void showBuilding(Move move) {
        Image house = new Image(Objects.requireNonNull(getClass().getResource("images/house.svg.png")).toString());
        this.view.setFill(new ImagePattern(house));
    }

    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}
    public void mark(){this.view.setFill(Color.rgb(0,0,255));}

    public void setAction(String action) {
        this.action = action;
    }
}