package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.MapEditorController;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.dto.UpdateMapTemplateDto;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.rest.MapApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.EditorConstants.RANDOM;

@Singleton
public class MapService {

    private final MapApiService mapApiService;

    private MapTemplate currentMap;

    private int currentMapSize;

    private final UserService userService;

    private MapEditorController mapEditorController;

    @Inject
    public MapService(MapApiService mapApiService, UserService userService) {
        this.mapApiService = mapApiService;
        this.userService = userService;
    }

    public Observable<MapTemplate> saveMap(List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.updateMap(currentMap._id(), new UpdateMapTemplateDto(null, null, "haha", tiles, harbors));
    }

    public Observable<MapTemplate> createMap(String name, String icon, String description, List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.createMap(new CreateMapTemplateDto(name, icon, description, tiles, harbors));
    }

    public void setCurrentMap(MapTemplate mapTemplate) {
        this.currentMap = mapTemplate;
    }

    public MapTemplate getCurrentMap() {
        return currentMap;
    }

    public void updateOrCreateMap(List<EditTile> editTiles, String name, String description) {
        List<HarborTemplate> harborTemplates = this.getHarbors(editTiles);
        List<TileTemplate> tileTemplates = this.getTiles(editTiles);
        // if the map is null, create a new one
        if (this.getCurrentMap() != null) {
            //if the map belongs to the current player, update it
            if (this.getCurrentMap().createdBy().equals(userService.getCurrentUser()._id())) {
                this.saveMap(tileTemplates, harborTemplates)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
            } else {
                //if the map belongs to someone else, create a new one
                this.createMap(name, null, description, tileTemplates, harborTemplates)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
            }
        } else {
            this.createMap(name, null, description, tileTemplates, harborTemplates)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(err -> handleSaveError())
                    .subscribe();
        }
    }

    public List<TileTemplate> getTiles(List<EditTile> editTiles) {
        List<TileTemplate> tiles = new ArrayList<>();
        for (EditTile et : editTiles) {
            if (!et.hexTile.type.equals("") && et.active) {
                int x = et.hexTile.q;
                int y = et.hexTile.s;
                int z = et.hexTile.r;
                String type = et.hexTile.type;
                if (type.equals(RANDOM)) {
                    type = null;
                }
                int number = et.hexTile.number;
                TileTemplate tileTemplate = new TileTemplate(x, y, z, type, number);
                tiles.add(tileTemplate);
            }
        }
        return tiles;
    }

    public List<HarborTemplate> getHarbors(List<EditTile> editTiles) {
        List<HarborTemplate> harbors = new ArrayList<>();
        for (EditTile et : editTiles) {
            //check if there is a harbor
            if (!et.currentHarborType.equals("") && et.active){
                int x = et.hexTile.q;
                int y = et.hexTile.s;
                int z = et.hexTile.r;
                String type = et.currentHarborType.replace("harbour_", "");
                //check if the harbour is a 3:1
                if (type.equals("general")) {
                    type = null;
                }
                int side = et.currentHarborSide;
                HarborTemplate harborTemplate = new HarborTemplate(x, y, z, type, side);
                harbors.add(harborTemplate);
            }
        }
        return harbors;
    }

    public List<EditTile> loadMap(int size) {
        MapTemplate mapTemplate = this.getCurrentMap();
        List<EditTile> editTiles = new ArrayList<>();
        if (mapTemplate == null) {
            return editTiles;
        }
        double scale = 100.0/size;
        boolean top = true;
        Polygon tile = mapEditorController.setView(scale);
        //set the tiles
        for (TileTemplate tt : mapTemplate.tiles()) {
            HexTile hexTile = new HexTile(tt.x(), tt.z(), tt.y(), scale, top);
            hexTile.type = tt.type();
            if (hexTile.type == null) {
                hexTile.type = RANDOM;
            }
            hexTile.number = tt.numberToken();
            ImageView numberView = mapEditorController.setNumberView(hexTile, scale);
            EditTile editTile = new EditTile(hexTile, tile, numberView, this.mapEditorController);
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
        //find out the size of the map
        int biggestCoordinate = 2;
        for (EditTile et : editTiles) {
            int q = et.hexTile.q;
            int r = et.hexTile.r;
            int s = et.hexTile.s;
            int x = Math.max(q, Math.max(r, s));
            biggestCoordinate = Math.max(biggestCoordinate, x);
            this.currentMapSize = Math.min(biggestCoordinate, 8);
        }
        return editTiles;
    }


    private void handleSaveError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Map-Saving-Error");
        alert.setContentText("Something went wrong");
        alert.showAndWait();
    }

    public void setMapEditorController(MapEditorController mapEditorController) {
        this.mapEditorController = mapEditorController;
    }

    public int getCurrentMapSize() {
        return this.currentMapSize;
    }
}
