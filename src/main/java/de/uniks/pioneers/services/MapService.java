package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.UpdateMapTemplateDto;
import de.uniks.pioneers.model.HarborTemplate;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.TileTemplate;
import de.uniks.pioneers.rest.MapApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MapService {

    private final MapApiService mapApiService;

    private MapTemplate currentMap;

    @Inject
    public MapService(MapApiService mapApiService) {
        this.mapApiService = mapApiService;
    }

    public Observable<MapTemplate> saveMap(List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.updateMap(currentMap._id(), new UpdateMapTemplateDto(null, null, tiles, harbors));
    }

    public void setCurrentMap(MapTemplate mapTemplate) {
        this.currentMap = mapTemplate;
    }

    public MapTemplate getCurrentMap() {
        return currentMap;
    }
}
