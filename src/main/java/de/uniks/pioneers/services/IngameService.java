package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class IngameService {

    private final PioneersApiService pioneersApiService;
    private final GameStorage gameStorage;

    @Inject
    public IngameService(PioneersApiService pioneersApiService, GameStorage gameStorage) {

        this.pioneersApiService = pioneersApiService;
        this.gameStorage = gameStorage;
    }

    public Observable<List<Player>> getAllPlayers(String gameId){
        return pioneersApiService.getAllPlayers(gameId);
    }

    public Observable<Map> getMap (String gameId){

        return pioneersApiService.getMap(gameId)
                .doOnNext(result -> gameStorage.setMap(result.tiles()));
    }

    public Observable<Player> getPlayer(String gameId, String playerId) {
        return pioneersApiService.getPlayer(gameId, playerId);
    }

    public Observable<State> getCurrentState(String gameId) {
        return pioneersApiService.getCurrentState(gameId);
    }


}
