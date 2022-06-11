package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javax.inject.Inject;
import java.util.ArrayList;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;
public class StreetPointController {

    private final GameService gameService;
    private final IngameService ingameService;

    private final UserService userService;
    public HexTile tile;
    private Circle view;
    private final CompositeDisposable disposable = new CompositeDisposable();
    // coordinates to be uploaded to the server as: x, y, z, side
    public int[] uploadCoords = new int[4];
    public ArrayList<BuildingPointController> adjacentBuildings = new ArrayList<>();
    SimpleIntegerProperty side = new SimpleIntegerProperty();
    private String action;

    private Building building;

    @Inject
    public StreetPointController(GameService gameService, IngameService ingameService, UserService userService) {
        this.gameService = gameService;
        this.ingameService = ingameService;
        this.userService = userService;
    }

    public void post(HexTile tile, Circle view) {
        this.tile = tile;
        this.view = view;
    }

    public void init() {
        this.view.setOnMouseClicked(this::placeStreet);
        this.view.setOnMouseEntered(this::dye);
        this.view.setOnMouseExited(this::undye);
    }

    public void placeStreet(MouseEvent mouseEvent) {
        boolean valid = false;

        for(BuildingPointController building : this.adjacentBuildings){
            if (building.getBuilding() != null && building.getBuilding().owner().equals(this.userService.getCurrentUser()._id())) {
                valid = true;
                break;
            }
            for(StreetPointController street : building.adjacentStreets){

                if((street != this) && street.building != null && street.building.owner().equals(this.userService.getCurrentUser()._id())){
                    valid = true;
                    break;
                }
            }
        }

        if (valid) {
            System.out.println("building street at: " + " " + uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3]);

            CreateBuildingDto newBuilding = new CreateBuildingDto(uploadCoords[0], uploadCoords[1], uploadCoords[2], uploadCoords[3], "road");
            disposable.add(ingameService.postMove(gameService.game.get()._id(), new CreateMoveDto(this.action, newBuilding))
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                        Pane fieldPane = (Pane) this.view.getScene().getRoot().lookup("#fieldPane");
                        fieldPane.getChildren().forEach(this::reset);
                    }));
        }
    }

    private void reset(Node node) {
        node.setOnMouseClicked(null);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    public void renderRoad(Building building) {
        Player player = gameService.players.get(building.owner());
        Rectangle road =  new Rectangle(60,7, Paint.valueOf(player.color()));
        Scene scene = view.getScene();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().add(road);
        road.setLayoutX(view.getLayoutX()-14);
        road.setLayoutY(view.getLayoutY()+12);
        if (building.side() == 3) {
            road.setRotate(90);
        } else if (building.side() == 7) {
            road.setRotate(30);
        }else {
            road.setRotate(-30);
        }
        this.building = building;
    }

    private void dye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(0, 255, 0));
    }

    private void undye(MouseEvent mouseEvent) {
        this.view.setFill(Color.rgb(255, 0, 0));
    }

    public String generateKeyString() {
        return uploadCoords[0] + " " + uploadCoords[1] + " " + uploadCoords[2] + " " + uploadCoords[3];
    }

    public ArrayList<BuildingPointController> getAdjacentBuildings() {
        return this.adjacentBuildings;
    }


    public void setAction(String action) {
        this.action = action;
    }
}
