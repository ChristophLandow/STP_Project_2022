package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.HotkeyController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.controller.subcontroller.SpeechSettingsController;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;

import static de.uniks.pioneers.Constants.ALT;
import static de.uniks.pioneers.Constants.STRG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsScreenControllerTest extends ApplicationTest {

    final ArrayList<Character> inputs = new ArrayList<>();
    final ArrayList<KeyCode> outputs = new ArrayList<>();
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

    @Mock(name = "mapBrowserControllerProvider")
    Provider<MapBrowserController> mapBrowserControllerProvider;

    @Mock
    StylesService stylesService;

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

    @InjectMocks MapBrowserController mapBrowserController;

    @Mock
    PrefService prefService;

    @Mock
    IngameService ingameService;
    @InjectMocks HotkeyController hotkeyController;

    @InjectMocks SettingsScreenController settingsScreenController;

    @Override
    public void start(Stage stage){
        Collections.addAll(inputs, 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','-',',','.','+','#','<');
        Collections.addAll(outputs, KeyCode.A,KeyCode.B,KeyCode.C,KeyCode.D,KeyCode.E,KeyCode.F,KeyCode.G,KeyCode.H,KeyCode.I,KeyCode.J,KeyCode.K,KeyCode.L,KeyCode.M,KeyCode.N,KeyCode.O,KeyCode.P,KeyCode.Q,KeyCode.R,KeyCode.S,KeyCode.T,KeyCode.U,KeyCode.V,KeyCode.W,KeyCode.X,KeyCode.Y,KeyCode.Z,KeyCode.DIGIT0,KeyCode.DIGIT1,KeyCode.DIGIT2,KeyCode.DIGIT3,KeyCode.DIGIT4,KeyCode.DIGIT5,KeyCode.DIGIT6,KeyCode.DIGIT7,KeyCode.DIGIT8,KeyCode.DIGIT9,KeyCode.MINUS,KeyCode.COMMA,KeyCode.PERIOD,KeyCode.PLUS,KeyCode.NUMBER_SIGN,KeyCode.LESS);
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
        when(mapBrowserControllerProvider.get()).thenReturn(mapBrowserController);
        when(prefService.saveTradeTextInput(any())).thenReturn("e");
        when(prefService.saveTradeChoiceBox(any())).thenReturn(STRG);
        when(prefService.saveEndChoiceBox(any())).thenReturn(ALT);
        when(prefService.saveEndTextInput(any())).thenReturn("e");
        when(prefService.saveRulesChoiceBox(any())).thenReturn(STRG);
        when(prefService.saveRulesTextInput(any())).thenReturn("0");
        when(prefService.saveSettingsChoiceBox(any())).thenReturn(STRG);
        when(prefService.saveSettingsTextInput(any())).thenReturn("1");
        when(prefService.getGenderVoice()).thenReturn("female");
        app.start(stage);
        app.show(settingsScreenController);
        hotkeyController.tradingChoiceBox = new ChoiceBox<>();
        hotkeyController.tradingTextField = new TextField();
        hotkeyController.endTurnChoiceBox = new ChoiceBox<>();
        hotkeyController.endTurnTextField = new TextField();
        hotkeyController.openSettingsChoiceBox = new ChoiceBox<>();
        hotkeyController.openSettingsTextField = new TextField();
        hotkeyController.openRulesTextField = new TextField();
        hotkeyController.openRulesChoiceBox = new ChoiceBox<>();
        hotkeyController.identicText = new Text();
        hotkeyController.scene = app.getStage().getScene();
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
        //change apperence Mode
        type(KeyCode.RIGHT);
        type(KeyCode.SPACE);
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
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.E);
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.DIGIT0);
        write("\t");
        type(KeyCode.SPACE);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        write("\t");
        type(KeyCode.DIGIT1);
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        write("\t");
        type(KeyCode.ENTER);
        //get saved data?
        verify(prefService, atLeastOnce()).getTradeChoiceBox();
        verify(prefService, atLeastOnce()).getEndChoiceBox();
        verify(prefService, atLeastOnce()).getRulesChoiceBox();
        verify(prefService, atLeastOnce()).getSettingsChoiceBox();
        verify(prefService, atLeastOnce()).getSettingsTextField();
        verify(prefService, atLeastOnce()).getTradeTextField();
        verify(prefService, atLeastOnce()).getRulesTextField();
        verify(prefService, atLeastOnce()).getEndTextField();
        //dos conversion works?
        int j = 0;
        for(Character i : inputs){
            assertEquals(hotkeyController.stringToKeyCode(i), outputs.get(j));
            j+=1;
        }
        //does save hotkeys work?
        hotkeyController.tradingTextField.setText("e");
        hotkeyController.tradingChoiceBox.setValue(STRG);
        hotkeyController.endTurnTextField.setText("e");
        hotkeyController.endTurnChoiceBox.setValue(ALT);
        hotkeyController.openSettingsTextField.setText("0");
        hotkeyController.openSettingsChoiceBox.setValue(STRG);
        hotkeyController.openRulesTextField.setText("1");
        hotkeyController.openRulesChoiceBox.setValue(STRG);
        hotkeyController.saveHotkeys();
        assertNotNull(hotkeyController.tradeHotkeyController);
        assertNotNull(hotkeyController.endTurnHotkeyController);
        assertNotNull(hotkeyController.openSettingsHotkeyController);
        assertNotNull(hotkeyController.openRulesHotkeyController);
        //..and with empty fields?
        hotkeyController.tradingTextField.setText("");
        hotkeyController.tradingChoiceBox.setValue("");
        hotkeyController.endTurnTextField.setText("");
        hotkeyController.endTurnChoiceBox.setValue("");
        hotkeyController.openSettingsTextField.setText("");
        hotkeyController.openSettingsChoiceBox.setValue("");
        hotkeyController.openRulesTextField.setText("");
        hotkeyController.saveHotkeys();
        assertNull(hotkeyController.tradeHotkeyController);
        assertNull(hotkeyController.endTurnHotkeyController);
        assertNull(hotkeyController.openSettingsHotkeyController);
        assertNull(hotkeyController.openRulesHotkeyController);
        verify(prefService, atLeastOnce()).deleteTradeHotkey();
        verify(prefService, atLeastOnce()).deleteSettingsHotkey();
        verify(prefService, atLeastOnce()).deleteEndHotkey();
        verify(prefService, atLeastOnce()).deleteRulesHotkey();
        verify(stylesService, atLeastOnce()).setStyleSheets(any(), anyString(), anyString());
    }
}