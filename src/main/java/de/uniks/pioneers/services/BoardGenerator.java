package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.Tile;

import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.GameConstants.scale;
import static java.lang.Math.*;

public class BoardGenerator {

    private final List<Tile> board = new ArrayList<>();
    private final List<Tile> corners = new ArrayList<>();


    public List<Tile> generateTiles(int size){

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;

                board.add(new Tile(q,r,s, scale));
            }
        }
        return this.board;
    }

    public List<Tile> generateCorners(int size){

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                if(!(((abs(q) + abs(r) + abs(s)) % 4 == 0) && (q % 2 == 0) && (r % 2 == 0) && (s % 2 == 0))){

                corners.add(new Tile(q,r,s, scale/2));
                }
            }
        }
        return this.corners;
    }






}
