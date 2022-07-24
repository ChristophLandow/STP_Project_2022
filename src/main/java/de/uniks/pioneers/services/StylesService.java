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

    public void setStyleSheets(ObservableList<String> styles, String styleSheetAddress, String styleSheetAddressDark, boolean lightMode, boolean darkMode) {
        // sets the Stylesheets for the app of a controller according to if a localStyleSheet is available
        // set both styleSheetAddresses to null if no local styleSheet is available
        if (styleSheetAddress != null) {
            styles.removeAll();
            if (lightMode) {
                styles.removeIf(style -> style.equals(STYLE_GLOBAL_DARK));
                styles.removeIf(style -> style.equals(styleSheetAddressDark));
                styles.addAll(STYLE_GLOBAL, styleSheetAddress);
            } else if (darkMode) {
                styles.removeIf(style -> style.equals(STYLE_GLOBAL));
                styles.removeIf(style -> style.equals(styleSheetAddress));
                styles.addAll(STYLE_GLOBAL_DARK, styleSheetAddressDark);
            }
        } else {
            styles.removeAll();
            if (lightMode) {
                styles.removeIf(style -> style.equals(STYLE_GLOBAL_DARK));
                styles.addAll(STYLE_GLOBAL);
            } else if (darkMode) {
                styles.removeIf(style -> style.equals(STYLE_GLOBAL));
                styles.addAll(STYLE_GLOBAL_DARK);
            }
        }
    }
}
