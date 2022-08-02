package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.services.BoardGenerator;
import de.uniks.pioneers.services.MapService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.GameConstants.RANDOM;


public class MapEditorController implements Controller{

    @FXML
    ImageView whaleImageView;
    @FXML
    ImageView iceImageView;
    @FXML
    ImageView fishImageView;
    @FXML
    ImageView randomImageView;
    @FXML
    ImageView desertImageView;
    @FXML
    ImageView icebearImageView;
    @FXML
    ImageView rockImageView;
    @FXML
    public Pane scrollPaneAnchorPane;
    @FXML
    Button buttonToMaps;
    @FXML
    Button buttonSave;

    @FXML
    Spinner<Integer> sizeSpinner;

    private final MapService mapService;
    public final BoardGenerator boardGenerator;

    public List<EditTile> tiles = new ArrayList<>();

    List<HexTile> frame = new ArrayList<>();

    final List<Polygon> tileViews = new ArrayList<>();

    public String selection = "";

    private final App app;

    private final Provider<MapBrowserController> mapBrowserControllerProvider;



    @Inject
    public MapEditorController(MapService mapService, App app, Provider<MapBrowserController> mapBrowserControllerProvider){

        this.mapService = mapService;
        this.app = app;
        this.mapBrowserControllerProvider = mapBrowserControllerProvider;
        this.boardGenerator = new BoardGenerator();

    }

    @Override
    public void init() {

        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, 2);

        this.sizeSpinner.setValueFactory(valueFactory);
        this.sizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            display(newValue);
            System.out.println("Spinner " + oldValue + " " + newValue);
        });

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
        init();
        this.tiles = loadMap(2);
        display(2);

        return parent;
    }

    private void display(int size){

        double scale = 100.0/size;
        this.frame = this.boardGenerator.buildEditorFrame(size, scale);

        this.scrollPaneAnchorPane.getChildren().removeAll(this.tileViews);
        this.tileViews.clear();
        this.selection = "";

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

            if(!hexTile.type.equals("")){
                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + hexTile.type + ".png")).toString());

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
        mapService.updateOrCreateMap(tiles);
        MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
        this.app.show(mapBrowserController);
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
        String[] numbers = {"2num", "3num", "4num", "5num", "6num", "8num", "9num", "10num", "11num", "12num"};
        return numbers[(int) (Math.random()*numbers.length)];
    }
    private String randomType(){
        String[] types = {"fields", "hills", "forest", "pasture", "mountains", "desert"};
        return types[(int) (Math.random()*types.length)];
    }

//-----SELECTION METHODS-----
    public void selectWhale() {
        this.selection = "fields";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.whaleImageView.setImage(image);}

    public void selectIce() {
        this.selection = "hills";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.iceImageView.setImage(image);}

    public void selectFish() {
        this.selection = "forest";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.fishImageView.setImage(image);}

    public void selectRandom() {
        this.selection = RANDOM;
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.randomImageView.setImage(image);}

    public void selectPolar() {
        this.selection = "pasture";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.icebearImageView.setImage(image);}

    public void selectCoal() {
        this.selection = "mountains";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.rockImageView.setImage(image);}

    public void selectDesert() {
        this.selection = "desert";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.desertImageView.setImage(image);}
    private void resetSelection(){
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
    }

    public void select2() {
        this.selection = "2num";
    }

    public void select3() {
        this.selection = "3num";
    }

    public void select4() {
        this.selection = "4num";
    }

    public void select5() {
        this.selection = "5num";
    }

    public void select6() {
        this.selection = "6num";
    }

    public void select8() {
        this.selection = "8num";
    }

    public void select9() {
        this.selection = "9num";
    }

    public void select10() {
        this.selection = "10num";
    }

    public void select11() {
        this.selection = "11num";
    }

    public void select12() {
        this.selection = "12num";
    }

    public void selectHarborGeneric() {
        this.selection = "harbour_general";
    }

    public void selectHarborFish() {
        this.selection = "harbour_lumber";
    }

    public void selectHarborIce() {
        this.selection = "harbour_brick";
    }

    public void selectHarborPolar() {
        this.selection = "harbour_wool";
    }

    public void selectHarborWhale() {
        this.selection = "harbour_grain";
    }

    public void selectHarborCoal() {
        this.selection = "harbour_ore";
    }

    public void selectDelete() {
        this.selection = "DELETE";
    }

    public List<EditTile> loadMap(int size) {
        MapTemplate mapTemplate = mapService.getCurrentMap();
        List<EditTile> editTiles = new ArrayList<>();
        if (mapTemplate == null) {
            return editTiles;
        }
        double scale = 100.0/size;
        boolean top = true;
        Polygon tile = setView(scale);
        //set the tiles
        for (TileTemplate tt : mapTemplate.tiles()) {
            HexTile hexTile = new HexTile(tt.x(), tt.y(), tt.z(), scale, top);
            hexTile.type = tt.type();
            hexTile.number = tt.numberToken();
            ImageView numberView = setNumberView(hexTile, scale);
            EditTile editTile = new EditTile(hexTile, tile, numberView, this);
            //set the harbors
            for (HarborTemplate ht : mapTemplate.harbors()) {
                //check for the right harbor with the coordinates
                if (tt.x() == ht.x() && tt.y() == ht.y() && tt.z() == ht.z()) {
                    if (ht.type() == null) {
                        editTile.currentHarborType = "harbour_general";
                    } else {
                        editTile.currentHarborType = "harbour_" + ht.type();
                    }
                    editTile.currentHarborSide = ht.side();
                    editTile.currentHarborOption = 0;
                }
            }
            editTiles.add(editTile);
        }
        return editTiles;
    }

    private ImageView setNumberView(HexTile hexTile, double scale) {
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

    private Polygon setView(double scale) {
        Polygon tile = new Polygon();
        tile.getPoints().addAll(0.0*scale, 1.0*scale,
                (Math.sqrt(3)/2)*scale,0.5*scale,
                (Math.sqrt(3)/2)*scale,-0.5*scale,
                0.0*scale,-1.0*scale,
                (-Math.sqrt(3)/2)*scale,-0.5*scale,
                (-Math.sqrt(3)/2)*scale,0.5*scale);
        return tile;
    }

}
