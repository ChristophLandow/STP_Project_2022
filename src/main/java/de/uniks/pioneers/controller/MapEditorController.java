package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.PopUpController.SaveMapPopUpController;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.BoardGenerator;
import de.uniks.pioneers.services.MapService;
import de.uniks.pioneers.services.StylesService;
import de.uniks.pioneers.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.EditorConstants.*;

public class MapEditorController implements Controller{
    @FXML public AnchorPane mapEditorAnchorPane;
    @FXML ImageView whaleImageView, iceImageView, fishImageView, randomImageView, desertImageView, icebearImageView, rockImageView;
    @FXML Circle Circle2, Circle3, Circle4, Circle5, Circle6, Circle8, Circle9, Circle10, Circle11, Circle12;
    @FXML ImageView harborFish, harborCoal, harborIce, harborPolar, harborWhale, harborGeneric;
    @FXML public Pane scrollPaneAnchorPane;
    @FXML Button buttonToMaps, buttonSave, deleteButton;
    @FXML Spinner<Integer> sizeSpinner;

    @Inject Provider<SaveMapPopUpController> saveMapPopUpControllerProvider;
    private final MapService mapService;

    private final UserService userService;
    public final BoardGenerator boardGenerator;

    private SaveMapPopUpController saveMapPopUpController;

    public List<EditTile> tiles = new ArrayList<>();

    List<HexTile> frame = new ArrayList<>();
    final List<Polygon> tileViews = new ArrayList<>();
    public String selection = "";
    public  String resMod = "";
    private final App app;

    private final StylesService stylesService;
    private final Provider<MapBrowserController> mapBrowserControllerProvider;


    @Inject
    public MapEditorController(MapService mapService, UserService userService, App app, Provider<MapBrowserController> mapBrowserControllerProvider, StylesService stylesService){

        this.mapService = mapService;
        this.userService = userService;
        this.app = app;
        this.mapBrowserControllerProvider = mapBrowserControllerProvider;
        this.stylesService = stylesService;
        this.boardGenerator = new BoardGenerator();
    }
    @Override
    public void init() {
        String darkStyle = "de/uniks/pioneers/styles/Darkmode_MapEditor.css";
        String style = "de/uniks/pioneers/styles/MapEditor.css";
        if (this.app.getStage().getScene() != null) {
            stylesService.setStyleSheets(this.app.getStage().getScene().getStylesheets(), style, darkStyle);
        }
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, mapService.getCurrentMapSize());
        this.sizeSpinner.setValueFactory(valueFactory);
        this.sizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> display(newValue));
        this.mapService.setMapEditorController(this);
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
        init();
        this.tiles = mapService.loadMap(5);

        // display for loading or for creating
        if (mapService.getCurrentMap() != null) {
            display(mapService.getCurrentMapSize());
        } else {
            display(2);
        }

        //add the popup to the pane
        this.saveMapPopUpController = saveMapPopUpControllerProvider.get();
        Node savePopUp = this.saveMapPopUpController.render();
        if (savePopUp != null) {
            this.mapEditorAnchorPane.getChildren().add(savePopUp);
        }
        return parent;
    }

    @Override
    public void stop() {
        saveMapPopUpController.stop();
    }
    private void display(int size){

        if(size > 3){this.resMod = "_low";}
        else{this.resMod = "";}
        double scale = 100.0/size;
        this.frame = this.boardGenerator.buildEditorFrame(size, scale);
        this.scrollPaneAnchorPane.getChildren().removeAll(this.tileViews);
        this.tileViews.clear();
        this.selection = "";
        resetSelectionUI();

        for(EditTile oldTile : this.tiles){oldTile.active = false;}
        for(HexTile hexTile : this.frame){

            int harborOption = 0;
            int harborSide = 0;
            String harborType = "";
            for(EditTile oldTile : this.tiles){
                oldTile.makeVisible(false);
                if((oldTile.hexTile.q == hexTile.q) & (oldTile.hexTile.r == hexTile.r) & (oldTile.hexTile.s == hexTile.s)){
                    hexTile.type = oldTile.hexTile.type;
                    hexTile.number = oldTile.hexTile.number;
                    this.selection = oldTile.currentHarborType;
                    harborOption = oldTile.currentHarborOption;
                    harborSide = oldTile.currentHarborSide;
                    harborType = oldTile.currentHarborType;
                    oldTile.destroy();
                    this.tiles.remove(oldTile);
                    break;
                }
            }
            Polygon tile = setView(scale);
            tile.setId(hexTile.q + "," + hexTile.r + "," + hexTile.s);

            if(!hexTile.type.equals("")){
                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + hexTile.type + resMod + ".png")).toString());

                tile.setFill(new ImagePattern(image));
            }else {
                tile.setFill(Paint.valueOf("#2D9BE7"));
                tile.setStroke(Paint.valueOf("#000000"));
            }
            ImageView numberView = setNumberView(hexTile, scale);

            tile.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2);
            tile.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2);

            this.scrollPaneAnchorPane.getChildren().add(tile);
            this.scrollPaneAnchorPane.getChildren().add(numberView);
            tile.toBack();
            numberView.toFront();
            this.tileViews.add(tile);

            EditTile newTile = new EditTile(hexTile, tile, numberView, this);
            if(harborSide != 0){
                newTile.currentHarborOption = harborOption;
                newTile.currentHarborSide = harborSide;
                newTile.currentHarborType = harborType;
                newTile.prepareHarborOptions();
                newTile.renderHarbor();}
            this.tiles.add(newTile);
        }

        this.sizeSpinner.toFront();
        this.buttonSave.toFront();
        this.buttonToMaps.toFront();
        this.selection = "";
    }
    public void toMaps(){
        MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
        this.app.show(mapBrowserController);
    }
    public void save(){
        MapTemplate currentMap = mapService.getCurrentMap();
        User currentUser = userService.getCurrentUser();
        this.saveMapPopUpController.setMapEditorController(this);
        this.saveMapPopUpController.setTiles(tiles);
        //check if the popup has to be opened or not
        if (currentMap == null || !currentMap.createdBy().equals(currentUser._id())) {
            //open popup
            this.saveMapPopUpController.showSavePopUp();
        } else {
            mapService.updateOrCreateMap(tiles, currentMap.name(), currentMap.description());
            MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
            this.app.show(mapBrowserController);
        }
    }
    public void randomize() {
        for(EditTile tile : this.tiles){
            if(tile.active){
                if(tile.hexTile.number == 0){
                    this.selection = randomNumber();
                    tile.place();}
                if(tile.hexTile.type.equals("")){
                    this.selection = randomType();
                    tile.place();}}
            this.selection = "";
        }
    }
    private String randomNumber(){
        String[] numbers = {select2, select3, select4, select5, select6, select8, select9, select10, select11, select12};
        return numbers[(int) (Math.random()*numbers.length)];
    }
    private String randomType(){
        String[] types = {FIELDS, HILLS, FOREST, PASTURE, MOUNTAINS, DESERT};
        return types[(int) (Math.random()*types.length)];
    }

//-----SELECTION METHODS-----
    public void selectWhale() {
        this.selection = FIELDS;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.whaleImageView.setImage(image);}
    public void selectIce() {
        this.selection = HILLS;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.iceImageView.setImage(image);}
    public void selectFish() {
        this.selection = FOREST;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.fishImageView.setImage(image);}
    public void selectRandom() {
        this.selection = RANDOM;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.randomImageView.setImage(image);}
    public void selectPolar() {
        this.selection = PASTURE;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.icebearImageView.setImage(image);}
    public void selectCoal() {
        this.selection = MOUNTAINS;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.rockImageView.setImage(image);}
    public void selectDesert() {
        this.selection = DESERT;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.desertImageView.setImage(image);}
    private void resetSelectionUI(){
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "fields" + ".png")).toString());
        this.whaleImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "hills" + ".png")).toString());
        this.iceImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "forest" + ".png")).toString());
        this.fishImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "pasture" + ".png")).toString());
        this.icebearImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "mountains" + ".png")).toString());
        this.rockImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "desert" + ".png")).toString());
        this.desertImageView.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "random" + ".png")).toString());
        this.randomImageView.setImage(image);
        this.Circle2.setFill(Paint.valueOf("1e90ff"));
        this.Circle3.setFill(Paint.valueOf("1e90ff"));
        this.Circle4.setFill(Paint.valueOf("1e90ff"));
        this.Circle5.setFill(Paint.valueOf("1e90ff"));
        this.Circle6.setFill(Paint.valueOf("1e90ff"));
        this.Circle8.setFill(Paint.valueOf("1e90ff"));
        this.Circle9.setFill(Paint.valueOf("1e90ff"));
        this.Circle10.setFill(Paint.valueOf("1e90ff"));
        this.Circle11.setFill(Paint.valueOf("1e90ff"));
        this.Circle12.setFill(Paint.valueOf("1e90ff"));
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_general.png")).toString());
        this.harborGeneric.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_fish.png")).toString());
        this.harborFish.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_iceberg.png")).toString());
        this.harborIce.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_polar-bear.png")).toString());
        this.harborPolar.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_whale.png")).toString());
        this.harborWhale.setImage(image);
        image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_coal.png")).toString());
        this.harborCoal.setImage(image);
    }
    public void select2() {
        this.selection = select2;
        resetSelectionUI();
        this.Circle2.setFill(Paint.valueOf("7FE766"));}
    public void select3(){
        this.selection = select3;
        resetSelectionUI();
        this.Circle3.setFill(Paint.valueOf("7FE766"));}
    public void select4(){
        this.selection = select4;
        resetSelectionUI();
        this.Circle4.setFill(Paint.valueOf("7FE766"));}
    public void select5(){
        this.selection = select5;
        resetSelectionUI();
        this.Circle5.setFill(Paint.valueOf("7FE766"));}
    public void select6(){
        this.selection = select6;
        resetSelectionUI();
        this.Circle6.setFill(Paint.valueOf("7FE766"));}
    public void select8(){
        this.selection = select8;
        resetSelectionUI();
        this.Circle8.setFill(Paint.valueOf("7FE766"));}
    public void select9(){
        this.selection = select9;
        resetSelectionUI();
        this.Circle9.setFill(Paint.valueOf("7FE766"));}
    public void select10(){
        this.selection = select10;
        resetSelectionUI();
        this.Circle10.setFill(Paint.valueOf("7FE766"));}
    public void select11(){
        this.selection = select11;
        resetSelectionUI();
        this.Circle11.setFill(Paint.valueOf("7FE766"));}
    public void select12(){
        this.selection = select12;
        resetSelectionUI();
        this.Circle12.setFill(Paint.valueOf("7FE766"));}
    public void selectHarborGeneric() {
        this.selection = HARBOUR_GENERAL;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_general_selected.png")).toString());
        this.harborGeneric.setImage(image);}
    public void selectHarborFish() {
        this.selection = HARBOUR_FISH;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_fish_selected.png")).toString());
        this.harborFish.setImage(image);}
    public void selectHarborIce() {
        this.selection = HARBOUR_ICE;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_iceberg_selected.png")).toString());
        this.harborIce.setImage(image);}
    public void selectHarborPolar() {
        this.selection = HARBOUR_POLAR;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_polar-bear_selected.png")).toString());
        this.harborPolar.setImage(image);}
    public void selectHarborWhale() {
        this.selection = HARBOUR_WHALE;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_whale_selected.png")).toString());
        this.harborWhale.setImage(image);}
    public void selectHarborCoal() {
        this.selection = HARBOUR_COAL;
        resetSelectionUI();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_coal_selected.png")).toString());
        this.harborCoal.setImage(image);}
    public void selectDelete() {
        this.selection = DELETE;
        resetSelectionUI();
    }

    public ImageView setNumberView(HexTile hexTile, double scale) {
        ImageView numberView = new ImageView();
        numberView.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2 - 33);
        numberView.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2 - 33);
        numberView.setScaleX(scale*0.01);
        numberView.setScaleY(scale*0.01);
        if(hexTile.number != 0){

            Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "tile_" + hexTile.number + ".png")).toString());
            numberView.setImage(image);
        }
       return numberView;
    }

    public Polygon setView(double scale) {
        Polygon tile = new Polygon();
        tile.getPoints().addAll(0.0*scale, scale,
                (Math.sqrt(3)/2)*scale,0.5*scale,
                (Math.sqrt(3)/2)*scale,-0.5*scale,
                0.0*scale,-1.0*scale,
                (-Math.sqrt(3)/2)*scale,-0.5*scale,
                (-Math.sqrt(3)/2)*scale,0.5*scale);
        return tile;
    }

}
