package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.dto.UpdateMapTemplateDto;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.rest.MapApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Alert;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class MapService {

    private final MapApiService mapApiService;

    private MapTemplate currentMap;

    private final UserService userService;

    @Inject
    public MapService(MapApiService mapApiService, UserService userService) {
        this.mapApiService = mapApiService;
        this.userService = userService;
    }

    public Observable<MapTemplate> saveMap(List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.updateMap(currentMap._id(), new UpdateMapTemplateDto(null, null, tiles, harbors));
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

    public void updateOrCreateMap(List<EditTile> editTiles) {
        List<HarborTemplate> harbors = new ArrayList<>();
        harbors.add(new HarborTemplate(0, 0, 0, "grain", 1));
        List<TileTemplate> tileTemplates = this.getTiles(editTiles);
        // if the map is null, create a new one
        if (this.getCurrentMap() != null) {
            //if the map belongs to the current player, update it
            if (this.getCurrentMap().createdBy().equals(userService.getCurrentUser()._id())) {
                this.saveMap(tileTemplates, harbors)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
            } else {
                //if the map belongs to someone else, create a new one
                this.createMap("Testus-Maximus-YEEEEE", null, "kein Bock auf Beschriebung", tileTemplates, harbors)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
            }
        } else {
            this.createMap("Testus-Maximus-ultra-deluxe", null, "kein Bock auf Beschriebung", tileTemplates, harbors)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(err -> handleSaveError())
                    .subscribe();
        }
    }

    public List<TileTemplate> getTiles(List<EditTile> editTiles) {
        List<TileTemplate> tiles = new ArrayList<>();
        for (EditTile et : editTiles) {
            int x = et.hexTile.q;
            int y = et.hexTile.r;
            int z = et.hexTile.s;
            String type = et.hexTile.type;
            int number = et.hexTile.number;
            TileTemplate tileTemplate = new TileTemplate(x, y, z, type, number);
            tiles.add(tileTemplate);
        }
        return tiles;
    }

    public List<HarborTemplate> getHarbors() {
        return null;
    }


    private void handleSaveError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Map-Saving-Error");
        alert.setContentText("You need to create this map first");
        alert.showAndWait();
    }
}
