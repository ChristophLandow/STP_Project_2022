package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class LobbyService {
    private final AuthApiService authApiService;
    private final GameApiService gameApiService;
    private final RefreshService refreshService;

    @Inject
    public LobbyService(AuthApiService authApiService, GameApiService gameApiService, RefreshService refreshService) {
        this.authApiService = authApiService;
        this.gameApiService = gameApiService;
        this.refreshService = refreshService;
    }

    public Observable<LogoutResult> logout() {
        this.refreshService.stopRefreshCycle();
        return authApiService.logout();
    }

    public Observable<List<Game>> getGames() {
        return gameApiService.getGames();
    }

    public Observable<Game> createGame(String name, boolean started, String password) {
        return gameApiService.create(new CreateGameDto(name, started, null, password));
    }
}
