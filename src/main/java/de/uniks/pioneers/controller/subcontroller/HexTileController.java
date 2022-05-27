package de.uniks.pioneers.controller.subcontroller;


import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.lang.reflect.Array;

import static de.uniks.pioneers.GameConstants.eulerC;
import static de.uniks.pioneers.GameConstants.scale;
import static java.lang.Math.sqrt;

public class HexTileController {

    private Polygon view;
    public Tile tile;

    public HexTileController(Tile tile, Polygon view){

        this.tile = tile;
        this.view = view;
        init();
    }

    public void init(){

        this.view.setOnMouseClicked(this::info);

    }

    public void yield(){
        //assigns resources
    }
    private void info(MouseEvent mouseEvent){


        System.out.println((tile.x + 1 * tile.scale) + " " + (0));
        System.out.println((tile.x - 1 * tile.scale) + " " + (0));
        System.out.println((tile.x + (sqrt(3)/2) * tile.scale) + " " + (tile.y + 0.5 * tile.scale));
        System.out.println((tile.x + (sqrt(3)/2) * tile.scale) + " " + (tile.y - 0.5 * tile.scale));
        System.out.println((tile.x - (sqrt(3)/2) * tile.scale) + " " + (tile.y + 0.5 * tile.scale));
        System.out.println((tile.x - (sqrt(3)/2) * tile.scale) + " " + (tile.y - 0.5 * tile.scale));
        System.out.println("\n");


        double[] A = tile.cartToCube((tile.x + 1 * tile.scale), 0, scale/2, true);
        double[] B = tile.cartToCube((tile.x - 1 * tile.scale), 0, scale/2, true);
        double[] C = tile.cartToCube((tile.x + (sqrt(3)/2) * tile.scale), (tile.y + 0.5 * tile.scale), scale/2, true);
        double[] D = tile.cartToCube((tile.x + (sqrt(3)/2) * tile.scale), (tile.y - 0.5 * tile.scale), scale/2, true);
        double[] E = tile.cartToCube((tile.x - (sqrt(3)/2) * tile.scale), (tile.y + 0.5 * tile.scale), scale/2, true);
        double[] F = tile.cartToCube((tile.x - (sqrt(3)/2) * tile.scale), (tile.y - 0.5 * tile.scale), scale/2, true);

        for (double v : A) {System.out.println(v);}
        System.out.println("\n");
        for (double v : B) {System.out.println(v);}
        System.out.println("\n");
        for (double v : C) {System.out.println(v);}
        System.out.println("\n");
        for (double v : D) {System.out.println(v);}
        System.out.println("\n");
        for (double v : E) {System.out.println(v);}
        System.out.println("\n");
        for (double v : F) {System.out.println(v);}
        System.out.println("\n");

        //System.out.println(tile);
    }

}
