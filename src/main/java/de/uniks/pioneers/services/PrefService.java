package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GameApiService;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.prefs.Preferences;
import static de.uniks.pioneers.Constants.*;

import static de.uniks.pioneers.Constants.REMEMBER_ME;

@Singleton
public class PrefService {
    private final Preferences preferences;
    private final TokenStorage tokenStorage;
    private final CryptService cryptService;
    private final GameApiService gameApiService;

    @Inject
    public PrefService(Preferences preferences, TokenStorage tokenStorage, CryptService cryptService, GameApiService gameApiService) {
        this.preferences = preferences;
        this.tokenStorage = tokenStorage;
        this.cryptService = cryptService;
        this.gameApiService = gameApiService;
    }

    public void remember() {
        String encryptedToken = this.cryptService.encrypt(tokenStorage.getRefreshToken());
        preferences.put(REMEMBER_ME, encryptedToken);
    }
    public void forget() {
        preferences.put(REMEMBER_ME, "");
    }

    public String recall() {
        String encryptedToken = preferences.get(REMEMBER_ME, "");
        if(encryptedToken.equals("")){
            return "";
        }
        return this.cryptService.decrypt(encryptedToken);
    }

    public void saveGameOnLeave(String id) {
        preferences.put("leavedGame", id);
    }

    public Game getSavedGame() {
        String leavedGameID = preferences.get("leavedGame", "");
        Game leavedGame = null;

        if(!leavedGameID.equals("")) {
            try {
                leavedGame = gameApiService.getGame(leavedGameID).blockingFirst();
            } catch (Exception e) {
                forgetSavedGame();
            }
        }

        return leavedGame;
    }

    private void forgetSavedGame() {
        preferences.put("leavedGame", "");
    }

    public void saveDarkModeState(String state){
        preferences.put("darkMode", state);
    }

    public boolean getDarkModeState(){
        return preferences.get("darkMode", "").equals(DARKMODE_TRUE);
    }
    public String saveTradeChoiceBox(String choice){
        preferences.put("tradeChoiceBox", choice);
        return choice;
    }

    public String saveTradeTextInput(String input){
        preferences.put("tradeTextField", input);
        return input;
    }

    public String saveEndChoiceBox(String choice){
        preferences.put("endChoiceBox", choice);
        return choice;
    }

    public String saveEndTextInput(String input){
        preferences.put("endTextField", input);
        return input;
    }

    public String saveSettingsChoiceBox(String choice){
        preferences.put("settingsChoiceBox", choice);
        return choice;
    }

    public String saveSettingsTextInput(String input){
        preferences.put("settingsTextField", input);
        return input;
    }

    public String saveRulesChoiceBox(String choice){
        preferences.put("rulesChoiceBox", choice);
        return choice;
    }

    public String saveRulesTextInput(String input){
        preferences.put("rulesTextField", input);
        return input;
    }

    public String getTradeChoiceBox(){
        return preferences.get("tradeChoiceBox", "");
    }

    public Character getTradeTextField(){
        return preferences.get("tradeTextField", "").charAt(0);
    }

    public String getEndChoiceBox(){
        return preferences.get("endChoiceBox", "");
    }

    public Character getEndTextField(){
        return preferences.get("endTextField", "").charAt(0);
    }

    public String getSettingsChoiceBox(){
        return preferences.get("settingsChoiceBox", "");
    }

    public Character getSettingsTextField(){
        return preferences.get("settingsTextField", "").charAt(0);
    }

    public String getRulesChoiceBox(){
        return preferences.get("rulesChoiceBox", "");
    }

    public Character getRulesTextField(){
        return preferences.get("rulesTextField", "").charAt(0);
    }
}