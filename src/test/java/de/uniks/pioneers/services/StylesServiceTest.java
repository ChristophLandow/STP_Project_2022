package de.uniks.pioneers.services;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.LoginScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static de.uniks.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StylesServiceTest {

    @Mock
    PrefService prefService;

    @InjectMocks
    StylesService stylesService;

    @Test
    void setStyleSheets() {
        ObservableList<String> style = FXCollections.observableArrayList();
        prefService.saveDarkModeState(DARKMODE_FALSE);
        stylesService.setStyleSheets(style);
        assertTrue(style.contains(STYLE_GLOBAL));
        when(prefService.getDarkModeState()).thenReturn(true);
        stylesService.setStyleSheets(style);
        assertTrue(style.contains(STYLE_GLOBAL_DARK));
    }

    @Test
    void testSetStyleSheets() {
        ObservableList<String> style = FXCollections.observableArrayList();
        prefService.saveDarkModeState(DARKMODE_FALSE);
        stylesService.setStyleSheets(style, "test", "testDark");
        assertTrue(style.contains(STYLE_GLOBAL));
        assertTrue(style.contains("test"));
        when(prefService.getDarkModeState()).thenReturn(true);
        stylesService.setStyleSheets(style, "test", "testDark");
        assertTrue(style.contains(STYLE_GLOBAL_DARK));
        assertTrue(style.contains("testDark"));
    }
}