package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.rest.GameMemberApiService;
import de.uniks.pioneers.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.List;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.LEAVE_GAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveGameControllerTest {
    @Mock
    App app;

    @Mock
    NewGameScreenLobbyController newGameScreenLobbyController;

    @Mock
    NewGameLobbyService newGameLobbyService;

    @Mock
    UserService userService;

    @Mock
    TokenStorage tokenStorage;

    @Mock
    CryptService cryptService;

    @Mock
    GameApiService gameApiService;

    @Spy
    PrefService prefService = new PrefService(Preferences.userNodeForPackage(this.getClass()), tokenStorage, cryptService, gameApiService);

    @Mock
    GameService gameService;

    @Mock
    TimerService timerService;

    @Mock
    GameStorage gameStorage;

    @Mock
    GameMemberApiService gameMemberApiService;

    @Mock
    GameChatController gameChatController;

    @Mock
    IngameScreenController ingameScreenController;

    @Mock(name = "lobbyScreenControllerProvider")
    Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Mock
    LobbyScreenController lobbyScreenController;

    @InjectMocks
    LeaveGameController leaveGameController;

    @Test
    void init() {
        leaveGameController.init(ingameScreenController, gameChatController);
        assertEquals(leaveGameController.gameChatController, gameChatController);
        assertEquals(leaveGameController.ingameScreenController, ingameScreenController);
    }

    @Test
    void saveLeavedGame() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        leaveGameController.saveLeavedGame("000", "111", 2, List.of(new User("000", "test1", "online", null)), "#ff0000");
        assertEquals(prefs.get(LEAVE_GAME, ""), "000");
        assertEquals(prefs.get("MapRadius", ""), "2");
        assertEquals(leaveGameController.users, List.of(new User("000", "test1", "online", null)));
        assertEquals(leaveGameController.myColor, "#ff0000");
        assertTrue(leaveGameController.leavedWithButton);
    }

    @Test
    void loadLeavedGame() {
        Game game = new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","000","TestGameA","001",1,false, null);
        leaveGameController.leavedWithButton = true;

        assertFalse(leaveGameController.loadLeavedGame(null));
        assertTrue(leaveGameController.loadLeavedGame(game));
    }

    @Test
    void leave() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        when(userService.getCurrentUser()).thenReturn(new User("321", "test2", "online", null));
        when((gameService.getGame())).thenReturn(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","123","TestGameB","001",1,false, null));
        when(gameStorage.getMapRadius()).thenReturn(5);
        when(lobbyScreenControllerProvider.get()).thenReturn(lobbyScreenController);
        leaveGameController.setKicked(false);
        leaveGameController.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        leaveGameController.ingameScreenController = ingameScreenController;
        leaveGameController.setOnClose(true);

        leaveGameController.leave();
        assertEquals(prefs.get(LEAVE_GAME, ""), "123");
        assertEquals(prefs.get("MapRadius", ""), "5");
        assertEquals(userService.isSpectator(), false);
    }

    @Test
    void leaveAfterVictory() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        when(userService.getCurrentUser()).thenReturn(new User("321", "test2", "online", null));
        when((gameService.getGame())).thenReturn(new Game("2022-05-18T18:12:58.114Z","2022-05-18T18:12:58.114Z","123","TestGameB","001",1,false, null));
        leaveGameController.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        leaveGameController.ingameScreenController = ingameScreenController;

        prefService.forgetSavedGame();
        leaveGameController.leaveAfterVictory();
        assertEquals(prefs.get(LEAVE_GAME, ""), "");
        assertEquals(prefs.get("MapRadius", ""), "");
        assertEquals(userService.isSpectator(), false);
    }
}
