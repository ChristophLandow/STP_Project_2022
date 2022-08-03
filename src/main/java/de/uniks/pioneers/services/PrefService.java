package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GameApiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.*;

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
        preferences.put(LEAVE_GAME, id);
    }

    public Game getSavedGame() {
        String leavedGameID = preferences.get(LEAVE_GAME, "");
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

    public void forgetSavedGame() {
        preferences.put(LEAVE_GAME, "");
        preferences.put("MapRadius", "");
        preferences.put("MapID", "");
    }

    public void saveDarkModeState(String state){
        preferences.put(DARK_MODE, state);
    }

    public boolean getDarkModeState(){
        return preferences.get(DARK_MODE, "").equals(DARKMODE_TRUE);
    }

    public void saveVoiceOutputActive(boolean isVoiceOutputActive) {
        preferences.put(VOICE_OUTPUT_ACTIVE, String.valueOf(isVoiceOutputActive));
    }

    public boolean getVoiceOutputActive() {
        //Set default value, if the preference is not set
        if(preferences.get(VOICE_OUTPUT_ACTIVE, "").equals("")){
            saveVoiceOutputActive(true);
        }

        return Boolean.parseBoolean(preferences.get(VOICE_OUTPUT_ACTIVE, ""));
    }

    public void saveGenderVoice(String gender){
        preferences.put(GENDER_VOICE, gender);
    }

    public String getGenderVoice(){
        //Set default value, if the preference is not set
        if(preferences.get(GENDER_VOICE, "").equals("")){
            saveGenderVoice(GameConstants.FEMALE);
        }

        return preferences.get(GENDER_VOICE, "");
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

    public String saveBuildStreetChoiceBox(String input){
        preferences.put("buildStreetTextField", input);
        return input;
    }

    public String saveBuildStreetTextInput(String input){
        preferences.put("buildStreetTextField", input);
        return input;
    }

    public String saveBuildIglooChoiceBox(String input){
        preferences.put("buildIglooTextField", input);
        return input;
    }

    public String saveBuildIglooTextInput(String input){
        preferences.put("buildIglooTextField", input);
        return input;
    }

    public String saveUpgradeIglooChoiceBox(String input){
        preferences.put("upgradeIglooTextField", input);
        return input;
    }

    public String saveUpgradeIglooTextInput(String input){
        preferences.put("upgradeIglooTextField", input);
        return input;
    }

    public String getTradeChoiceBox(){
        return preferences.get("tradeChoiceBox", "");
    }

    public Character getTradeTextField(){
        if(preferences.get("tradeTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("tradeTextField", "").charAt(0);
        }
    }

    public String getBuildStreetChoiceBox(){
        return preferences.get("buildStreetChoiceBox", "");
    }

    public Character getBuildStreetTextField(){
        if(preferences.get("buildStreetTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("buildStreetTextField", "").charAt(0);
        }
    }

    public String getBuildIglooChoiceBox(){
        return preferences.get("buildIgluChoiceBox", "");
    }

    public Character getBuildIglooTextField(){
        if(preferences.get("buildIgluTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("buildIgluTextField", "").charAt(0);
        }
    }

    public String getUpgradeIglooChoiceBox(){
        return preferences.get("upgradedIgluChoiceBox", "");
    }

    public Character getUpgradeIglooTextField(){
        if(preferences.get("upgradeIgluTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("upgradeIgluTextField", "").charAt(0);
        }
    }

    public String getEndChoiceBox(){
        return preferences.get("endChoiceBox", "");
    }

    public Character getEndTextField(){
        if(preferences.get("endTextField", "").equals("")){
           return Character.MIN_VALUE;
        } else {
            return preferences.get("endTextField", "").charAt(0);
        }
    }

    public String getSettingsChoiceBox(){
        return preferences.get("settingsChoiceBox", "");
    }

    public Character getSettingsTextField(){
        if(preferences.get("settingsTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("settingsTextField", "").charAt(0);
        }

    }

    public String getRulesChoiceBox(){
        return preferences.get("rulesChoiceBox", "");
    }

    public Character getRulesTextField(){
        if(preferences.get("rulesTextField", "").equals("")){
            return Character.MIN_VALUE;
        } else {
            return preferences.get("rulesTextField", "").charAt(0);
        }
    }

    public void deleteTradeHotkey(){
        preferences.put("tradeTextField", "");
        preferences.put("tradeChoiceBox", "");
    }

    public void deleteEndHotkey(){
        preferences.put("endTextField", "");
        preferences.put("endChoiceBox", "");
    }

    public void deleteSettingsHotkey(){
        preferences.put("settingsTextField", "");
        preferences.put("settingsChoiceBox", "");
    }

    public void deleteRulesHotkey(){
        preferences.put("rulesTextField", "");
        preferences.put("rulesChoiceBox", "");
    }

    public void deleteBuildStreetHotkey(){
        preferences.put("buildStreetTextField", "");
        preferences.put("buildStreetChoiceBox", "");
    }

    public void deleteBuildIglooHotkey(){
        preferences.put("buildIglooTextField", "");
        preferences.put("buildIglooChoiceBox", "");
    }

    public void deleteUpgradeIglooHotkey(){
        preferences.put("upgradeIglooTextField", "");
        preferences.put("upgradeIglooChoiceBox", "");
    }

    public void setVoteButtonState(String mapId, String state){
        preferences.put(mapId,state);
    }

    public Boolean getVoteButtonState(String mapId){
        return preferences.get(mapId, "").equals(VOTED);
    }
}