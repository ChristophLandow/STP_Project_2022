package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.Tile;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BoardGenerator {

    private final List<Tile> board = new ArrayList<Tile>();


    public List<Tile> generate(int size){

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;

                board.add(new Tile(q,r,s));


            }

        }


        for(int i = 0; i <this.board.size(); i++){

            System.out.println(this.board.get(i));
        }


        return this.board;
    }






}
