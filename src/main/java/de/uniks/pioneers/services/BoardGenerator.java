package de.uniks.pioneers.services;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.model.TileTemplate;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.GameConstants.eulerC;
import static java.lang.Math.*;

public class BoardGenerator {
    private final List<HexTile> board = new ArrayList<>();
    private final List<HexTile> edges = new ArrayList<>();
    private final List<HexTile> corners = new ArrayList<>();

    private final List<HexTile> harbors = new ArrayList<>();

    public List<HexTile> generateTiles(List<Tile> tiles, double hexScale) {

        for(Tile tile : tiles) {

            HexTile newHexTile = new HexTile(tile.x(), tile.z(), tile.y(), hexScale, true);
            newHexTile.setGameInfo(tile.type(), tile.numberToken());
            board.add(newHexTile);
        }
        return this.board;
    }

    public List<HexTile> generateHarbors(List<Harbor> harborList, double hexScale) {

        for(Harbor harbor : harborList) {
            HexTile newHexTile = new HexTile(harbor.x(), harbor.z(), harbor.y(), hexScale, true);
            newHexTile.setGameInfo(harbor.type(), harbor.side());
            harbors.add(newHexTile);
        }
        return this.harbors;
    }

    public List<HexTile> generateEdges(int size, double hexScale) {
        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){
                int s = -q-r;
                if(!(((abs(q) + abs(r) + abs(s)) % 4 == 0) && (q % 2 == 0) && (r % 2 == 0) && (s % 2 == 0))){
                    edges.add(new HexTile(q, r, s, hexScale / 2, true));
                }
            }
        }
        return this.edges;
    }

    public List<HexTile> generateCorners(int size, double hexScale) {

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                int maxSize = max(max(q, r), s);
                int i = maxSize - min(min(q, r), s);
                if(i % 3 != 0){
                    if(i < 2 * size -((size-1)/2)+1){
                        corners.add(new HexTile(q,r,s, hexScale * eulerC, false));
                    }
                }
            }
        }
        return this.corners;
    }

    public List<HexTile> buildEditorFrame(int size, double scale) {

        List<HexTile> frame = new ArrayList<>();

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                frame.add(new HexTile(q,r,s, scale, true));

            }
        }
        return frame;
    }

    public ImageView getHarborImage(String type) {
        if (type == null) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_general.png")).toString());
        } else if (type.equals("ore")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_coal.png")).toString());
        } else if (type.equals("brick")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_iceberg.png")).toString());
        } else if (type.equals("wool")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_polar-bear.png")).toString());
        } else if (type.equals("lumber")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_fish.png")).toString());
        } else if (type.equals("grain")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbour_whale.png")).toString());
        } else if (type.equals("random")) {
            return new ImageView(Objects.requireNonNull(Main.class.getResource("controller/ingame/harbor_random.png")).toString());
        } else {
            return null;
        }
    }

    public ImageView placeHarbor(double x, double y, ImageView image, int side, double width, double height, double scale) {

        double x_plus = x + width / 2 - scale / 2 + 0.75 * scale;
        double x_minus = x + width / 2 - scale / 2 - 0.75 * scale;
        double y_plus = -y + height / 2 - scale / 2 + 1.25 * scale;
        double y_minus = -y + height / 2 - scale / 2 - 1.25 * scale;
        if (side == 1) {
            image.setLayoutX(x_plus);
            image.setLayoutY(y_minus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(30);
        } else if (side == 3) {
            image.setLayoutX(x + width / 2 - scale / 2 + 1.5 * scale);
            image.setLayoutY(-y + height / 2 - scale / 2);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(90);
        } else if (side == 5) {
            image.setLayoutX(x_plus);
            image.setLayoutY(y_plus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(150);
        } else if (side == 7) {
            image.setLayoutX(x_minus);
            image.setLayoutY(y_plus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(210);
        } else if (side == 9) {
            image.setLayoutX(x + width / 2 - scale / 2 - 1.5 * scale);
            image.setLayoutY(-y + height / 2 - scale / 2);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(270);
        } else if (side == 11) {
            image.setLayoutX(x_minus);
            image.setLayoutY(y_minus);
            image.setFitHeight(scale);
            image.setFitWidth(scale);
            image.rotateProperty().set(330);
        }
        return image;
    }

    public List<HexTile> generateTileTemplates(List<TileTemplate> tiles, double hexScale) {
        List<HexTile> mapTiles = new ArrayList<>();
        for(TileTemplate tile : tiles) {

            HexTile newHexTile = new HexTile(tile.x(), tile.z(), tile.y(), hexScale, true);
            newHexTile.setGameInfo(tile.type(), tile.numberToken());
            mapTiles.add(newHexTile);
        }
        return mapTiles;
    }

    public List<HexTile> generateHarborTemplates(List<HarborTemplate> harbors, double hexScale) {
        List<HexTile> harborList = new ArrayList<>();
        for(HarborTemplate harbor : harbors) {
            HexTile newHexTile = new HexTile(harbor.x(), harbor.z(), harbor.y(), hexScale, true);
            newHexTile.setGameInfo(harbor.type(), harbor.side());
            harborList.add(newHexTile);
        }
        return harborList;
    }
}
