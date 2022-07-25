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

    @Inject
    public MapService(MapApiService mapApiService) {
        this.mapApiService = mapApiService;
    }

    private Observable<MapTemplate> saveMap(String id, List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return mapApiService.updateMap(id, new UpdateMapTemplateDto(null, null, tiles, harbors));
    }
}
