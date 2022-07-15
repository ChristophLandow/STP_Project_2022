package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.Tile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.*;

class BoardGeneratorTest {


    final BoardGenerator boardGenerator = new BoardGenerator();

    @Test
    void generateTiles() {

        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile(0,0,0,"desert",12));

        tiles.add(new Tile(0,1,-1,"pasture",2));
        tiles.add(new Tile(0,-1,1,"fields",3));
        tiles.add(new Tile(1,-1,0,"forest",4));
        tiles.add(new Tile(-1,1,0,"hills",5));
        tiles.add(new Tile(1,0,-1,"mountains",6));
        tiles.add(new Tile(-1,0,1,"pasture",7));

        List<HexTile> board = boardGenerator.generateTiles(tiles, 75);

        assertEquals(board.size(), 7);

        List<int[]> takenCoords = new ArrayList<>();

        for(HexTile hexTile : board){
            assert((hexTile.q + hexTile.r + hexTile.s == 0));
            assert((max(max(abs(hexTile.q), abs(hexTile.r)), abs(hexTile.s)) < 2));

            int[] coords = new int[]{hexTile.q, hexTile.r, hexTile.s};
            assert(!takenCoords.contains(coords));

            takenCoords.add(coords);
        }
    }

    @Test
    void generateEdges() {

        List<HexTile> edges = boardGenerator.generateEdges(3, 75);

        assertEquals(edges.size(), 30);

        List<int[]> takenCoords = new ArrayList<>();

        for(HexTile hexTile : edges){
            assert((hexTile.q + hexTile.r + hexTile.s == 0));
            assert((max(max(abs(hexTile.q), abs(hexTile.r)), abs(hexTile.s)) < 4));

            int[] coords = new int[]{hexTile.q, hexTile.r, hexTile.s};
            assert(!takenCoords.contains(coords));

            takenCoords.add(coords);
        }

    }

    @Test
    void generateCorners() {

        List<HexTile> corners = boardGenerator.generateCorners(3, 75);

        assertEquals(corners.size(), 24);

        List<int[]> takenCoords = new ArrayList<>();

        for(HexTile hexTile : corners){
            assert((hexTile.q + hexTile.r + hexTile.s == 0));
            assert((max(max(abs(hexTile.q), abs(hexTile.r)), abs(hexTile.s)) < 4));

            int[] coords = new int[]{hexTile.q, hexTile.r, hexTile.s};
            assert(!takenCoords.contains(coords));

            takenCoords.add(coords);
        }
    }
}