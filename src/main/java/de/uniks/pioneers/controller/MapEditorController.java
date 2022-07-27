package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.services.EditorManager;
import de.uniks.pioneers.services.MapService;
import de.uniks.pioneers.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;


public class MapEditorController implements Controller{

    @FXML
    ImageView whaleImageView;
    @FXML
    ImageView iceImageView;
    @FXML
    ImageView fishImageView;
    @FXML
    ImageView blankFieldImageView;
    @FXML
    ImageView icebearImageView;
    @FXML
    ImageView rockImageView;
    @FXML
    Pane scrollPaneAnchorPane;
    @FXML
    Button buttonToMaps;
    @FXML
    Button buttonSave;

    EditorManager editorManager;

    private final MapService mapService;


    @Inject
    Provider<MapBrowserController> mapBrowserControllerProvider;

    List<EditTile> tiles = new ArrayList<>();

    public String selection = "";

    private final App app;



    @Inject
    public MapEditorController(EditorManager editorManager, MapService mapService, UserService userService, App app){

        this.editorManager = editorManager;
        this.mapService = mapService;
        this.app = app;
        init();

    }

    @Override
    public void init() {



    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MapEditorScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent parent;
        try {
            parent =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        display(2);

        return parent;
    }

    private void display(int size){

        int scale = 50;

        List<HexTile> frame = this.editorManager.buildFrame(size, scale);

        for(HexTile hexTile : frame){

            Polygon tile = new Polygon();
            tile.getPoints().addAll(0.0*scale, 1.0*scale,
                    (Math.sqrt(3)/2)*scale,0.5*scale,
                    (Math.sqrt(3)/2)*scale,-0.5*scale,
                    0.0*scale,-1.0*scale,
                    (-Math.sqrt(3)/2)*scale,-0.5*scale,
                    (-Math.sqrt(3)/2)*scale,0.5*scale);

            tile.setFill(Paint.valueOf("#ffffff"));
            tile.setStroke(Paint.valueOf("#000000"));

            tile.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2);
            tile.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2);

            this.scrollPaneAnchorPane.getChildren().add(tile);

            this.tiles.add(new EditTile(hexTile, tile, this));

        }


    }

    public void toMaps(){
    }
    public void save(ActionEvent event){
        mapService.updateOrCreateMap();
        MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
        this.app.show(mapBrowserController);
    }


    public void selectWhale(MouseEvent mouseEvent) {this.selection = "fields";}

    public void selectIce(MouseEvent mouseEvent) {this.selection = "hills";}

    public void selectFish(MouseEvent mouseEvent) {this.selection = "forest";}

    public void selectRandom(MouseEvent mouseEvent) {this.selection = RANDOM;}

    public void selectPolar(MouseEvent mouseEvent) {this.selection = "pasture";}

    public void selectCoal(MouseEvent mouseEvent) {this.selection = "mountains";}

    public void selectDesert(MouseEvent mouseEvent) {this.selection = "desert";}

    private void handleSaveError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Map-Saving-Error");
        alert.setContentText("You need to create this map first");
        alert.showAndWait();
    }
}
