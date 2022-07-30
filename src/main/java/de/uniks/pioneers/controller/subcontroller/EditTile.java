package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.MapEditorController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Objects;

public class EditTile {

    public HexTile hexTile;

    Polygon view;

    ImageView numberView;

    ImageView harbourView = new ImageView();

    MapEditorController mapEditorController;

    ArrayList<Integer> harbourOptions = new ArrayList<>();

    //index of the harbor position in the array list
    public int currentHarborOption = 0;

    public int currentHarborSide = 0;
    public String currentHarborType = "";

    public EditTile(HexTile hexTile, Polygon view, ImageView numberView, MapEditorController mapEditorController){

        this.hexTile = hexTile;
        this.view = view;
        this.numberView = numberView;

        this.mapEditorController = mapEditorController;

        init();

    }

    private void init(){

        this.view.setOnMouseClicked(this::place);
    }

    private void place(MouseEvent mouseEvent) {

        makeVisible(true);

        if(!this.mapEditorController.selection.equals("")) {

            if(this.mapEditorController.selection.equals("DELETE")){
                this.view.setFill(Paint.valueOf("#2D9BE7"));
                this.view.setStroke(Paint.valueOf("#000000"));
                this.hexTile.type = "";

                return;
            }

            if(this.mapEditorController.selection.endsWith("num")){
                this.hexTile.number = Integer.parseInt(this.mapEditorController.selection.replace("num", ""));
                Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + "tile_" + this.hexTile.number + ".png")).toString());
                this.numberView.setImage(image);
                this.numberView.toFront();
                return;
            }
            if(this.mapEditorController.selection.contains("harbour")){
                handleHarbor();
                return;
            }
            Image image = new Image(Objects.requireNonNull(Main.class.getResource("controller/ingame/" + this.mapEditorController.selection + ".png")).toString());

            this.view.setFill(new ImagePattern(image));
            this.hexTile.type = this.mapEditorController.selection;
        }
    }

    public void handleHarbor(){

        if(this.hexTile.type.equals("")){return;}

        //remove invalid positions if function is called for first time
        prepareHarborOptions();

        this.mapEditorController.scrollPaneAnchorPane.getChildren().remove(this.harbourView);

        cleanHarborOptions();

        this.currentHarborType = this.mapEditorController.selection;

        this.currentHarborOption = (this.currentHarborOption + 1) % this.harbourOptions.size();
        currentHarborSide = this.harbourOptions.get(this.currentHarborOption);

        if(this.harbourOptions.get(this.currentHarborOption) != 0) {
        renderHarbor();
        }
        else{
            this.currentHarborType = "";
        }

        }

        public void renderHarbor(){

            String harborType = this.currentHarborType.replace("harbour_", "");
            if(harborType.equals("general")){
                harborType = null;
            }
            ImageView image = this.mapEditorController.boardGenerator.getHarborImage(harborType);

            System.out.println(this.currentHarborSide);
            this.harbourView = this.mapEditorController.boardGenerator.placeHarbor(this.hexTile.x, this.hexTile.y, image, this.currentHarborSide,
                    this.mapEditorController.scrollPaneAnchorPane.getPrefWidth(),
                    this.mapEditorController.scrollPaneAnchorPane.getPrefHeight(),
                    this.hexTile.scale);
            System.out.println("harborview "+ this.harbourView);
            this.mapEditorController.scrollPaneAnchorPane.getChildren().add(this.harbourView);

        }

        public void prepareHarborOptions(){
            if(this.harbourOptions.isEmpty()){
                this.harbourOptions.add(0);
                this.harbourOptions.add(1);
                this.harbourOptions.add(3);
                this.harbourOptions.add(5);
                this.harbourOptions.add(7);
                this.harbourOptions.add(9);
                this.harbourOptions.add(11);}
        }

        private void cleanHarborOptions(){
            for(EditTile tile : this.mapEditorController.tiles){

                if(tile.hexTile.type.equals("")){continue;}

                if((this.hexTile.q +1 == tile.hexTile.q) & (this.hexTile.r -1 == tile.hexTile.r)){
                    this.harbourOptions.remove(Integer.valueOf(1));}
                if((this.hexTile.q +1 == tile.hexTile.q) & (this.hexTile.s -1 == tile.hexTile.s)){
                    this.harbourOptions.remove(Integer.valueOf(3));}
                if((this.hexTile.r +1 == tile.hexTile.r) & (this.hexTile.s -1 == tile.hexTile.s)){
                    this.harbourOptions.remove(Integer.valueOf(5));}
                if((this.hexTile.q -1 == tile.hexTile.q) & (this.hexTile.r +1 == tile.hexTile.r)){
                    this.harbourOptions.remove(Integer.valueOf(7));}
                if((this.hexTile.q -1 == tile.hexTile.q) & (this.hexTile.s +1 == tile.hexTile.s)){
                    this.harbourOptions.remove(Integer.valueOf(9));}
                if((this.hexTile.r -1 == tile.hexTile.r) & (this.hexTile.s +1 == tile.hexTile.s)){
                    this.harbourOptions.remove(Integer.valueOf(11));}
            }
        }
        public void destroy(){
        if(this.numberView != null){this.numberView.setImage(null);}
        if(this.harbourView != null){this.harbourView.setImage(null);}
        }

        public void makeVisible(boolean visibility){
            if(this.numberView != null){
                this.numberView.setVisible(visibility);}
            if(this.harbourView != null){
                this.harbourView.setVisible(visibility);}
        }

    }

