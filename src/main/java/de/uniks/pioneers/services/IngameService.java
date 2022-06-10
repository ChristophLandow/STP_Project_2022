package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public Observable<Player> getPlayer(String gameId, String playerId) {
        return pioneersApiService.getPlayer(gameId, playerId);
    }

    public Observable<List<Building>> getAllBuildings(String gameId){
        return pioneersApiService.getAllBuildings(gameId);
    }


    public Observable<Map> getMap (String gameId){

        return pioneersApiService.getMap(gameId)
                .doOnNext(result -> gameStorage.setMap(result.tiles()));
    }


    public Observable<State> getCurrentState(String gameId) {
        return pioneersApiService.getCurrentState(gameId);
    }

    public Observable<Move> postMove(String gameId, CreateMoveDto dto) {
        return pioneersApiService.postMove(gameId, dto);
    }
}
