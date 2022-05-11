package de.uniks.pioneers.services;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class PrefService{

    private final Preferences preferences;

    private final TokenStorage tokenStorage;

    private final CryptService cryptService;

    @Inject
    public PrefService(Preferences preferences, TokenStorage tokenStorage, CryptService cryptService){

        this.preferences = preferences;
        this.tokenStorage = tokenStorage;
        this.cryptService = cryptService;
    }

    public void remember(){

        String encryptedToken = this.cryptService.encrypt(tokenStorage.getRefreshToken());
        preferences.put("RememberMe", encryptedToken);
    }
    public void forget(){

        preferences.put("RememberMe", "");
    }

    public String recall(){

        String encryptedToken = preferences.get("RememberMe", "");
        if(encryptedToken.equals("")){
            return "";
        }
        return this.cryptService.decrypt(encryptedToken);
    }
}
