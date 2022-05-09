package de.uniks.pioneers.services;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class PrefService{

    private final Preferences preferences;

    private final TokenStorage tokenStorage;

    @Inject
    public PrefService(Preferences preferences, TokenStorage tokenStorage){

        this.preferences = preferences;
        this.tokenStorage = tokenStorage;
    }

    public void remember(){

        preferences.put("RememberMe", tokenStorage.getRefreshToken());
    }
    public void forget(){

        preferences.put("RememberMe", "");
    }

    public String recall(){

        return preferences.get("RememberMe", "");
    }
}
