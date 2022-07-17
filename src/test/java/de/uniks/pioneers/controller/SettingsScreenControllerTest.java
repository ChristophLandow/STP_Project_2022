package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.HotkeyController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.controller.subcontroller.SpeechSettingsController;
import de.uniks.pioneers.services.PrefService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsScreenControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Mock(name = "ingameScreenControllerProvider")
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @Mock(name = "newGameLobbyControllerProvider")
    Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider;

    @Mock(name = "editProfileControllerProvider")
    Provider<EditProfileController> editProfileControllerProvider;

    @Mock(name = "chatControllerProvider")
    Provider<ChatController> chatControllerProvider;

    @Mock(name = "lobbyScreenControllerProvider")
    Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Mock(name = "loginScreenControllerProvider")
    Provider<LoginScreenController> loginScreenControllerProvider;

    @Mock(name = "rulesScreenControllerProvider")
    Provider<RulesScreenController> rulesScreenControllerProvider;

    @Mock(name = "lobbyUserlistControllerProvider")
    Provider<LobbyUserlistController> lobbyUserlistControllerProvider;

    @Mock(name = "lobbyGameListControllerProvider")
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @Mock(name = "speechSettingsControllerProvider")
    Provider<SpeechSettingsController> speechSettingsControllerProvider;

    @InjectMocks IngameScreenController ingameScreenController;
    @InjectMocks NewGameScreenLobbyController newGameScreenLobbyController;
    @InjectMocks EditProfileController editProfileController;
    @InjectMocks ChatController chatController;
    @InjectMocks LobbyScreenController lobbyScreenController;
    @InjectMocks LoginScreenController loginScreenController;
    @InjectMocks RulesScreenController rulesScreenController;
    @InjectMocks LobbyUserlistController lobbyUserlistController;
    @InjectMocks LobbyGameListController lobbyGameListController;
    @InjectMocks SpeechSettingsController speechSettingsController;

    @Mock
    PrefService prefService;

    @Mock HotkeyController hotkeyController;

    @InjectMocks SettingsScreenController settingsScreenController;

    @Override
    public void start(Stage stage){
        when(ingameScreenControllerProvider.get()).thenReturn(ingameScreenController);
        when(newGameLobbyControllerProvider.get()).thenReturn(newGameScreenLobbyController);
        when(editProfileControllerProvider.get()).thenReturn(editProfileController);
        when(chatControllerProvider.get()).thenReturn(chatController);
        when(lobbyScreenControllerProvider.get()).thenReturn(lobbyScreenController);
        when(loginScreenControllerProvider.get()).thenReturn(loginScreenController);
        when(rulesScreenControllerProvider.get()).thenReturn(rulesScreenController);
        when(lobbyUserlistControllerProvider.get()).thenReturn(lobbyUserlistController);
        when(lobbyGameListControllerProvider.get()).thenReturn(lobbyGameListController);
        when(speechSettingsControllerProvider.get()).thenReturn(speechSettingsController);

        when(prefService.getGenderVoice()).thenReturn("female");

        app.start(stage);
        app.show(settingsScreenController);
    }

    @Test
    void test() {
        type(KeyCode.SPACE);

        verify(ingameScreenControllerProvider, atLeastOnce()).get();
        verify(newGameLobbyControllerProvider).get();
        verify(lobbyScreenControllerProvider).get();
        verify(chatControllerProvider).get();
        verify(editProfileControllerProvider).get();
        verify(loginScreenControllerProvider).get();
        verify(rulesScreenControllerProvider).get();

        //Select music
        write("\t");
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        //Select voice output
        write("\t");
        write("\t");
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.ENTER);


        //Set Trade hotkey
        write("\t");
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.E);
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        type(KeyCode.ENTER);

        verify(prefService, atLeastOnce()).getTradeChoiceBox();
        verify(prefService, atLeastOnce()).getEndChoiceBox();
        verify(prefService, atLeastOnce()).getRulesChoiceBox();
    }
}