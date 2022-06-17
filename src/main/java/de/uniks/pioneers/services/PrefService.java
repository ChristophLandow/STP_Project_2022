package de.uniks.pioneers.services;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GameApiService;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.prefs.Preferences;

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
}
