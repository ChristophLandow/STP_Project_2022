package de.uniks.pioneers.services;

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

    public Observable<MapTemplate> createMap(String name, String icon, List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.createMap(new CreateMapTemplateDto(name, icon, tiles, harbors));
    }

    public void setCurrentMap(MapTemplate mapTemplate) {
        this.currentMap = mapTemplate;
    }

    public MapTemplate getCurrentMap() {
        return currentMap;
    }

    public void updateOrCreateMap() {
        //TODO: get the actual Mapdetails
        List<TileTemplate> tiles = new ArrayList<>();
        tiles.add(new TileTemplate(0, 0, 0, "desert", 12));
        List<HarborTemplate> harbors = new ArrayList<>();
        harbors.add(new HarborTemplate(0, 0, 0, "grain", 1));
        if (this.getCurrentMap() != null) {
            if (this.getCurrentMap().createdBy().equals(userService.getCurrentUser()._id())) {
                this.saveMap(tiles, harbors)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
            } else {
                //TODO: create the map
                this.createMap("Testus-Maximus-YEEEEE", null, tiles, harbors)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(err -> handleSaveError())
                        .subscribe();
                System.out.println("nicht deine Map");
            }
        } else {
            this.createMap("Testus-Maximus-ultra-deluxe", null, tiles, harbors)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(err -> handleSaveError())
                    .subscribe();
            System.out.println("neue Map erstellt");
        }
    }

    private void handleSaveError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Map-Saving-Error");
        alert.setContentText("You need to create this map first");
        alert.showAndWait();
    }
}
