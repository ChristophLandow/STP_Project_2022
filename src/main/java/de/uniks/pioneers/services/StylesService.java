package de.uniks.pioneers.services;

import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.STYLE_GLOBAL;
import static de.uniks.pioneers.Constants.STYLE_GLOBAL_DARK;

@Singleton
public class StylesService {

    @Inject PrefService prefService;

    @Inject
    public StylesService(PrefService prefService) {
        this.prefService = prefService;
    }

    public void setStyleSheets(ObservableList<String> screen) {
        if (prefService.getDarkModeState()) {
            screen.removeIf((style -> style.equals(STYLE_GLOBAL)));
            screen.addAll(STYLE_GLOBAL_DARK);
        } else {
            screen.removeIf((style -> style.equals(STYLE_GLOBAL_DARK)));
            screen.addAll(STYLE_GLOBAL);
        }
    }

    public void setStyleSheets(ObservableList<String> screen, String localStyle, String localStyleDark) {
        if (prefService.getDarkModeState()) {
            screen.removeIf((style -> style.equals(STYLE_GLOBAL)));
            screen.addAll(STYLE_GLOBAL_DARK, localStyleDark);
        } else {
            screen.removeIf((style -> style.equals(STYLE_GLOBAL_DARK)));
            screen.addAll(STYLE_GLOBAL, localStyle);
        }
    }
}
