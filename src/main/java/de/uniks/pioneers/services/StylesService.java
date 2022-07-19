package de.uniks.pioneers.services;

import dagger.Provides;
import de.uniks.pioneers.Constants;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.styleGlobal;
import static de.uniks.pioneers.Constants.styleGlobalDark;

@Singleton
public class StylesService {

    @Inject PrefService prefService;

    @Inject
    public StylesService(PrefService prefService) {
        this.prefService = prefService;
    }

    public void setStyleSheets(ObservableList<String> screen) {
        if (prefService.getDarkModeState()) {
            screen.removeIf((style -> style.equals(styleGlobal)));
            screen.addAll(styleGlobalDark);
        } else {
            screen.removeIf((style -> style.equals(styleGlobalDark)));
            screen.addAll(styleGlobal);
        }
    }

    public void setStyleSheets(ObservableList<String> screen, String localStyle, String localStyleDark) {
        if (prefService.getDarkModeState()) {
            screen.removeIf((style -> style.equals(styleGlobal)));
            screen.addAll(styleGlobalDark, localStyleDark);
        } else {
            screen.removeIf((style -> style.equals(styleGlobalDark)));
            screen.addAll(styleGlobal, localStyle);
        }
    }
}
