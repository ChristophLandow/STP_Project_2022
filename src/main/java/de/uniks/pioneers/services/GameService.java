package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameService {

    private final GameApiService gameApiService;

    @Inject
    public GameService(GameApiService gameApiService) {
        this.gameApiService = gameApiService;
    }

    public Observable<Game> deleteGame(String gameId) {
        return gameApiService.delete(gameId);
    }

}
