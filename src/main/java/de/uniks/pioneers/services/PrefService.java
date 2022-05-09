package de.uniks.pioneers.services;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class PrefService{

    private final Preferences preferences;

    @Inject
    public PrefService(Preferences preferences){

        this.preferences = preferences;
    }

    public void remember(String refreshToken){

        preferences.put("RememberMe", refreshToken);
    }
    public void forget(){

        preferences.put("RememberMe", "");
    }

    public String recall(){

        return preferences.get("RememberMe", "");
    }
}
