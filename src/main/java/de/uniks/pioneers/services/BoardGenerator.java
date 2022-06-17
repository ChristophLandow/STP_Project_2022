package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.Tile;

import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.GameConstants.eulerC;
import static de.uniks.pioneers.GameConstants.scale;
import static java.lang.Math.*;

public class BoardGenerator {

    private final List<HexTile> board = new ArrayList<>();
    private final List<HexTile> edges = new ArrayList<>();
    private final List<HexTile> corners = new ArrayList<>();

    public List<HexTile> generateTiles(List<Tile> tiles){

        for(Tile tile : tiles) {

            HexTile newHexTile = new HexTile(tile.x(), tile.z(), tile.y(), scale, true);
            newHexTile.setGameInfo(tile.type(), tile.numberToken());
            board.add(newHexTile);
        }
        return this.board;
    }
    public List<HexTile> generateEdges(int size){

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                if(!(((abs(q) + abs(r) + abs(s)) % 4 == 0) && (q % 2 == 0) && (r % 2 == 0) && (s % 2 == 0))){

                    edges.add(new HexTile(q,r,s, scale/2, true));
                }
            }
        }
        return this.edges;
    }
    public List<HexTile> generateCorners(int size){

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                if((max(max(q, r), s) - min(min(q, r), s)) % 3 != 0){
                    if((max(max(q, r), s) - min(min(q, r), s)) < 2 * size -((size-1)/2)+1){
                    corners.add(new HexTile(q,r,s, scale * eulerC, false));
}
                }
            }
        }
        return this.corners;
    }
}
