package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class IngameService {

    private final PioneersApiService pioneersApiService;

    @Inject
    public IngameService(PioneersApiService pioneersApiService) {
        this.pioneersApiService = pioneersApiService;
    }

    public Observable<List<Player>> getAllPlayers(String gameId){
        return pioneersApiService.getAllPlayers(gameId);
    }

    public Observable<Map> getMap (String gameId){
        return pioneersApiService.getMap(gameId);
    }


}
