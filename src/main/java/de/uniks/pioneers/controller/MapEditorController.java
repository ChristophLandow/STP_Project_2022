package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.services.BoardGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import static de.uniks.pioneers.GameConstants.*;


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
    Pane scrollPaneAnchorPane;
    @FXML
    Button buttonToMaps;
    @FXML
    Button buttonSave;

    @FXML
    Spinner<Integer> sizeSpinner;

    BoardGenerator boardGenerator;

    List<EditTile> tiles = new ArrayList<>();

    List<HexTile> frame = new ArrayList<>();

    List<Polygon> tileViews = new ArrayList<>();

    public String selection = "";



    @Inject
    public MapEditorController(){

        this.boardGenerator = new BoardGenerator();

    }

    @Override
    public void init() {

        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, 2);

        this.sizeSpinner.setValueFactory(valueFactory);
        this.sizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            display(newValue);
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
        display(2);

        return parent;
    }

    private void display(int size){

        System.out.println(this.tiles.size());
        System.out.println(this.frame.size());
        System.out.println(this.tileViews.size());

        double scale = 100.0/size;

        this.frame = this.boardGenerator.buildEditorFrame(size, scale);

        this.scrollPaneAnchorPane.getChildren().removeAll(this.tileViews);
        this.tileViews.clear();

        for(HexTile hexTile : this.frame){

            for(EditTile oldTile : this.tiles){
                if((oldTile.hexTile.q == hexTile.q) & (oldTile.hexTile.r == hexTile.r) & (oldTile.hexTile.s == hexTile.s)){
                    hexTile.type = oldTile.hexTile.type;
                    hexTile.number = oldTile.hexTile.number;
                    this.tiles.remove(oldTile);
                    break;
                }
            }

            Polygon tile = new Polygon();
            tile.getPoints().addAll(0.0*scale, 1.0*scale,
                    (Math.sqrt(3)/2)*scale,0.5*scale,
                    (Math.sqrt(3)/2)*scale,-0.5*scale,
                    0.0*scale,-1.0*scale,
                    (-Math.sqrt(3)/2)*scale,-0.5*scale,
                    (-Math.sqrt(3)/2)*scale,0.5*scale);

            if(hexTile.type != ""){
                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + hexTile.type + ".png")).toString());

                tile.setFill(new ImagePattern(image));
            }else {
                tile.setFill(Paint.valueOf("#ffffff"));
                tile.setStroke(Paint.valueOf("#000000"));
            }
            ImageView numberView = new ImageView();
            numberView.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2 - 335);
            numberView.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2 - 335);
            numberView.setScaleX(0.03);
            numberView.setScaleY(0.03);
            if(hexTile.number != 0){

                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "tile_" + hexTile.number + ".png")).toString());
                numberView.setImage(image);
                numberView.toFront();
            }

            tile.setLayoutX(hexTile.x + this.scrollPaneAnchorPane.getPrefWidth() / 2);
            tile.setLayoutY(-hexTile.y + this.scrollPaneAnchorPane.getPrefHeight() / 2);

            this.scrollPaneAnchorPane.getChildren().add(tile);
            this.scrollPaneAnchorPane.getChildren().add(numberView);
            this.tileViews.add(tile);

            this.tiles.add(new EditTile(hexTile, tile, numberView, this));
        }

        this.sizeSpinner.toFront();
        this.buttonSave.toFront();
        this.buttonToMaps.toFront();
    }

    public void toMaps(){
    }
    public void save(){}


    public void selectWhale(MouseEvent mouseEvent) {
        this.selection = "fields";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.whaleImageView.setImage(image);}

    public void selectIce(MouseEvent mouseEvent) {
        this.selection = "hills";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.iceImageView.setImage(image);}

    public void selectFish(MouseEvent mouseEvent) {
        this.selection = "forest";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.fishImageView.setImage(image);}

    public void selectRandom(MouseEvent mouseEvent) {
        this.selection = RANDOM;}

    public void selectPolar(MouseEvent mouseEvent) {
        this.selection = "pasture";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.icebearImageView.setImage(image);}

    public void selectCoal(MouseEvent mouseEvent) {
        this.selection = "mountains";
        resetSelection();
        Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.selection + "_selected" + ".png")).toString());
        this.rockImageView.setImage(image);}

    public void selectDesert(MouseEvent mouseEvent) {
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
    }

    public void select2(MouseEvent mouseEvent) {
        this.selection = "2num";
    }

    public void select3(MouseEvent mouseEvent) {
        this.selection = "3num";
    }

    public void select4(MouseEvent mouseEvent) {
        this.selection = "4num";
    }

    public void select5(MouseEvent mouseEvent) {
        this.selection = "5num";
    }

    public void select6(MouseEvent mouseEvent) {
        this.selection = "6num";
    }

    public void select8(MouseEvent mouseEvent) {
        this.selection = "8num";
    }

    public void select9(MouseEvent mouseEvent) {
        this.selection = "9num";
    }

    public void select10(MouseEvent mouseEvent) {
        this.selection = "10num";
    }

    public void select11(MouseEvent mouseEvent) {
        this.selection = "11num";
    }

    public void select12(MouseEvent mouseEvent) {
        this.selection = "12num";
    }
}
