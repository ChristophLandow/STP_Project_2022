package de.uniks.pioneers.services;

import javax.inject.Inject;
import java.util.prefs.Preferences;

import static de.uniks.pioneers.Constants.REMEMBER_ME;

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
        preferences.put(REMEMBER_ME, encryptedToken);
    }
    public void forget(){

        preferences.put(REMEMBER_ME, "");
    }

    public String recall(){

        String encryptedToken = preferences.get(REMEMBER_ME, "");
        if(encryptedToken.equals("")){
            return "";
        }
        return this.cryptService.decrypt(encryptedToken);
    }
}
