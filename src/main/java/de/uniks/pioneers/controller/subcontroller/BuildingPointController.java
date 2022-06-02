package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.IngameScreenController;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Objects;

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
        CreateBuildingDto newBuilding = new CreateBuildingDto(tile.q, tile.r, tile.r, 6, buildingType);
        disposable.add(ingameService.postMove(gameId, new CreateMoveDto(this.action, newBuilding))
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    this.showBuilding(move);
                    this.reset();
                }));

    }

    private void showBuilding(Move move) {
//        Image house = new Image(Objects.requireNonNull(getClass().getResource("images/house.svg.png")).toString());
//        this.view.setFill(new ImagePattern(house));
        SVGPath houseSVG = new SVGPath();
        houseSVG.setContent("M 12.679688 11.316406 L 0.3125 22.601562 L 0.152344 23.316406 C 0.0078125 23.996094 0.0078125 24.074219 0.179688 24.75 C 0.554688 26.203125 0.546875 26.195312 1.332031 26.578125 C 1.992188 26.902344 2.09375 26.929688 3.253906 26.988281 C 3.933594 27.015625 4.914062 27.074219 5.453125 27.101562 L 6.414062 27.160156 L 6.414062 48.835938 L 6.8125 49.40625 L 7.21875 49.972656 L 14.292969 49.980469 L 21.367188 49.980469 L 21.914062 49.492188 L 22.453125 49.007812 L 22.394531 42.230469 L 22.332031 35.453125 L 25.953125 35.472656 L 29.574219 35.503906 L 29.539062 41.914062 C 29.5 49.226562 29.480469 49.023438 30.226562 49.644531 L 30.632812 49.980469 L 37.066406 49.980469 C 44.613281 49.980469 44.359375 50.007812 45.167969 49.140625 L 45.660156 48.613281 L 45.660156 27.027344 L 46.914062 27.082031 C 47.800781 27.132812 48.453125 27.113281 49.199219 27.007812 L 50.226562 26.863281 L 51.046875 26.023438 L 51.859375 25.179688 L 51.914062 24.273438 L 51.972656 23.367188 L 49.386719 20.738281 L 46.792969 18.109375 L 46.792969 4.148438 L 45.886719 3.25 L 39.945312 3.183594 L 34 3.125 L 33.507812 3.394531 L 33.027344 3.660156 L 32.972656 5.628906 L 30 2.914062 C 26.6875 -0.113281 27.160156 0.191406 25.566406 0.0664062 L 25.046875 0.0195312 Z M 12.679688 11.316406");
        houseSVG.setScaleX(0.7);
        houseSVG.setScaleY(0.7);
        houseSVG.setLayoutX(view.getLayoutX()-fieldpane.getLayoutX());
        houseSVG.setLayoutY(view.getLayoutY()-fieldpane.getLayoutY());
        this.fieldpane.getChildren().add(houseSVG);

        System.out.println("house x: " + houseSVG.getLayoutX());
        System.out.println("house y: " + houseSVG.getLayoutY());
    }

    private void dye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(0,255,0));}
    private void undye(MouseEvent mouseEvent){this.view.setFill(Color.rgb(255,0,0));}
    public void mark(){this.view.setFill(Color.rgb(0,0,255));}

    public void setAction(String action) {
        this.action = action;
    }
}