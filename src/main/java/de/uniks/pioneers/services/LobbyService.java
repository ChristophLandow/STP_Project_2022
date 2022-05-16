package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class LobbyService {
    private final AuthApiService authApiService;
    private final GameApiService gameApiService;


    public SimpleObjectProperty <ObservableList<Game>> gamesProperty;
    private final ObservableList<Game> games = FXCollections.observableArrayList();

    @Inject
    public LobbyService(AuthApiService authApiService, GameApiService gameApiService) {
        this.authApiService = authApiService;
        this.gameApiService = gameApiService;
    }

    public Observable<LogoutResult> logout() {
        return authApiService.logout();
    }

    public Observable<List<Game>> getGames() {
        return gameApiService.getGames();
    }

    public Observable<Game> createGame(String name, String password) {
        return gameApiService.create(new CreateGameDto(name, password));
    }
}
