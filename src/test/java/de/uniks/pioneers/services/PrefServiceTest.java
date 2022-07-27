package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.GameConstants.FEMALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrefServiceTest {

    @Mock
    Preferences preferences;
    @Mock
    CryptService cryptService;
    @Mock
    TokenStorage tokenStorage;
    @Mock
    GameApiService gameApiService;
    @InjectMocks
    PrefService prefService;

    @Test
    void remember() {
        String refreshToken = "gdsiaukgewho215r32nefwd";
        tokenStorage.setRefreshToken(refreshToken);
        prefService.remember();
        assertEquals(cryptService.encrypt(refreshToken), preferences.get(REMEMBER_ME, "false"));
    }

    @Test
    void forget() {
        String refreshToken = "gdsiaukgewho215r32nefwd";
        tokenStorage.setRefreshToken(refreshToken);
        prefService.remember();
        assertEquals(cryptService.encrypt(refreshToken), preferences.get(REMEMBER_ME, "false"));
        prefService.forget();
        assertNull(preferences.get(REMEMBER_ME, "false"));
    }

    @Test
    void recall() {
       when(preferences.get(anyString(), anyString())).thenReturn("");
       String result = prefService.recall();
       assertEquals("", result);
       when(preferences.get(anyString(), anyString())).thenReturn("gdaoi52nwe");
       when(cryptService.decrypt(anyString())).thenReturn("gdaoi52nwe");
       result = prefService.recall();
       assertEquals("gdaoi52nwe", result);
    }

    @Test
    void saveGameOnLeave() {
        String id = "fdsr3rwef4z";
        prefService.saveGameOnLeave(id);
        when(preferences.get(eq(LEAVE_GAME), anyString())).thenReturn(id);
        assertEquals(id, preferences.get(LEAVE_GAME, "false"));
    }

    @Test
    void saveMapRadiusOnLeave() {
        int mapRadius = 4;
        prefService.saveMapRadiusOnLeave(mapRadius);
        when(preferences.get(eq("MapRadius"), anyString())).thenReturn("4");
        assertEquals("4", preferences.get("MapRadius", "test"));
    }

    @Test
    void getSavedGame() {
        when(gameApiService.getGame(anyString())).thenReturn(Observable.just(new Game("yesterday", "now", "test", "testGame", "me", 2, true, new GameSettings(2,2, null, true, 0))));
        when(preferences.get(anyString(),anyString())).thenReturn("test");
        Game game = prefService.getSavedGame();
        assertEquals("test", game._id());
    }

    @Test
    void getSavedMapRadius() {
        when(preferences.get(eq("MapRadius"), anyString())).thenReturn("");
        int result = prefService.getSavedMapRadius();
        assertEquals(-1, result);
        when(preferences.get(eq("MapRadius"), anyString())).thenReturn("3");
        result = prefService.getSavedMapRadius();
        assertEquals(3, result);
    }

    @Test
    void saveDarkModeState() {
        when(preferences.get(eq(DARK_MODE), anyString())).thenReturn(DARKMODE_FALSE);
        prefService.saveDarkModeState(DARKMODE_FALSE);
        assertFalse(prefService.getDarkModeState());
    }

    @Test
    void getDarkModeState() {
        when(preferences.get(eq(DARK_MODE), anyString())).thenReturn(DARKMODE_TRUE);
        boolean result = prefService.getDarkModeState();
        assertTrue(result);
    }

    @Test
    void VoiceOutputActive() {
        when(preferences.get(eq(VOICE_OUTPUT_ACTIVE), anyString())).thenReturn("TRUE");
        prefService.saveVoiceOutputActive(true);
        assertTrue(prefService.getVoiceOutputActive());
    }

    @Test
    void GenderVoice() {
        when(preferences.get(eq(GENDER_VOICE), anyString())).thenReturn(FEMALE);
        prefService.saveGenderVoice(FEMALE);
        assertEquals(FEMALE, prefService.getGenderVoice());
    }

    @Test
    void TradeChoiceBox() {
        when(preferences.get(eq("tradeChoiceBox"), anyString())).thenReturn("true");
        prefService.saveTradeChoiceBox("true");
        assertEquals("true", prefService.getTradeChoiceBox());
    }

    @Test
    void TradeTextInput() {
        when(preferences.get(eq("tradeTextField"), anyString())).thenReturn("f");
        prefService.saveTradeTextInput("f");
        assertEquals('f', prefService.getTradeTextField());
    }

    @Test
    void EndChoiceBox() {
        when(preferences.get(eq("endChoiceBox"), anyString())).thenReturn("false");
        prefService.saveEndChoiceBox("false");
        assertEquals("false", prefService.getEndChoiceBox());
    }

    @Test
    void EndTextInput() {
        when(preferences.get(eq("endTextField"), anyString())).thenReturn("l");
        prefService.saveEndTextInput("l");
        assertEquals('l', prefService.getEndTextField());
    }

    @Test
    void SettingsChoiceBox() {
        when(preferences.get(eq("settingsChoiceBox"), anyString())).thenReturn("false");
        prefService.saveSettingsChoiceBox("false");
        assertEquals("false", prefService.getSettingsChoiceBox());
    }

    @Test
    void SettingsTextInput() {
        when(preferences.get(eq("settingsTextField"), anyString())).thenReturn("a");
        prefService.saveSettingsTextInput("a");
        assertEquals('a', prefService.getSettingsTextField());
    }

    @Test
    void RulesChoiceBox() {
        when(preferences.get(eq("rulesChoiceBox"), anyString())).thenReturn("true");
        prefService.saveRulesChoiceBox("true");
        assertEquals("true", prefService.getRulesChoiceBox());
    }

    @Test
    void RulesTextInput() {
        when(preferences.get(eq("rulesTextField"), anyString())).thenReturn("m");
        prefService.saveRulesTextInput("m");
        assertEquals('m', prefService.getRulesTextField());
    }

    @Test
    void deleteTradeHotkey() {
        when(preferences.get(anyString(), anyString())).thenReturn("");
        prefService.deleteTradeHotkey();
        assertEquals(Character.MIN_VALUE, prefService.getTradeTextField());
        assertEquals("", prefService.getTradeChoiceBox());
    }

    @Test
    void deleteEndHotkey() {
        when(preferences.get(anyString(), anyString())).thenReturn("");
        prefService.deleteEndHotkey();
        assertEquals(Character.MIN_VALUE, prefService.getEndTextField());
        assertEquals("", prefService.getEndChoiceBox());
    }

    @Test
    void deleteSettingsHotkey() {
        when(preferences.get(anyString(), anyString())).thenReturn("");
        prefService.deleteSettingsHotkey();
        assertEquals(Character.MIN_VALUE, prefService.getSettingsTextField());
        assertEquals("", prefService.getSettingsChoiceBox());
    }

    @Test
    void deleteRulesHotkey() {
        when(preferences.get(anyString(), anyString())).thenReturn("");
        prefService.deleteRulesHotkey();
        assertEquals(Character.MIN_VALUE, prefService.getRulesTextField());
        assertEquals("", prefService.getRulesChoiceBox());
    }
}