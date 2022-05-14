package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {
    @Mock
    AuthApiService authApiService;

    @Mock
    GameApiService gameApiService;

    @InjectMocks
    LobbyService lobbyService;

    @Test
    void logout() {
        when(authApiService.logout()).thenReturn(Observable.just(new LogoutResult()));
        lobbyService.logout();
        verify(authApiService).logout();
    }

    @Test
    void getGames() {
        Game testGame = new Game("1","2","3","name1","owner1", 1);
        Game testGame2 = new Game("1","2","3","name2","owner2", 1);
        List<Game> testGameList = new ArrayList<>();
        testGameList.add(testGame);
        testGameList.add(testGame2);
        when(gameApiService.getGames()).thenReturn(Observable.just(testGameList));

        final List<Game> result = lobbyService.getGames().blockingFirst();
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).name(), "name1");
        assertEquals(result.get(1).owner(), "owner2");

        verify(gameApiService).getGames();
    }

    @Test
    void createGame() {
        Game testGame = new Game("1","2","3","n","o", 1);
        when(gameApiService.create(any())).thenReturn(Observable.just(testGame));

        final Game result = lobbyService.createGame("n","p").blockingFirst();
        assertEquals(result.name(),"n");

        verify(gameApiService).create(new CreateGameDto("n","p"));
    }
}