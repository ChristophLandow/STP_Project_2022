package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.MapEditorController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.util.Objects;

public class EditTile {

    public HexTile hexTile;

    Polygon view;

    ImageView numberView;

    ImageView harbourView;

    MapEditorController mapEditorController;

    ArrayList<Integer> harbourOptions = new ArrayList<>();

    int currentHarborOption = 0;
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

        if(!this.mapEditorController.selection.equals("")) {

            if(this.mapEditorController.selection.endsWith("num")){
                this.hexTile.number = Integer.parseInt(this.mapEditorController.selection.replace("num", ""));
                System.out.println("controller/ingame/" + "tile_" + this.hexTile.number + ".png");
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

    private void handleHarbor(){

        if(this.hexTile.type.equals("")){return;}

        //remove invalid positions if function is called for first time
        if(this.harbourOptions.isEmpty()){
            this.harbourOptions.add(0);
            this.harbourOptions.add(1);
            this.harbourOptions.add(3);
            this.harbourOptions.add(5);
            this.harbourOptions.add(7);
            this.harbourOptions.add(9);
            this.harbourOptions.add(11);}

        this.currentHarborOption = (this.currentHarborOption + 1) % this.harbourOptions.size();

        this.mapEditorController.scrollPaneAnchorPane.getChildren().remove(this.harbourView);

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
        this.currentHarborType = this.mapEditorController.selection;
        renderHarbor();

        }

        public void renderHarbor(){
            if(this.harbourOptions.get(this.currentHarborOption) != 0) {

                String harborType = this.currentHarborType.replace("harbour_", "");
                if(harborType.equals("general")){
                    harborType = null;
                }
                ImageView image = this.mapEditorController.boardGenerator.getHarborImage(harborType);

                this.harbourView = this.mapEditorController.boardGenerator.placeHarbor(this.hexTile.x, this.hexTile.y, image, this.harbourOptions.get(this.currentHarborOption),
                        this.mapEditorController.scrollPaneAnchorPane.getPrefWidth(),
                        this.mapEditorController.scrollPaneAnchorPane.getPrefHeight(),
                        this.hexTile.scale);
                this.mapEditorController.scrollPaneAnchorPane.getChildren().add(this.harbourView);
            }
            else{
                this.currentHarborType = "";
            }
        }

    }

