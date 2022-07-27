package de.uniks.pioneers.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static de.uniks.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void settingsSetStyleSheets() {
        ObservableList<String> style = FXCollections.observableArrayList();
        stylesService.setStyleSheets(style, null, null, true, false);
        assertTrue(style.contains(STYLE_GLOBAL));
        stylesService.setStyleSheets(style, null, null, false, true);
        assertTrue(style.contains(STYLE_GLOBAL_DARK));
        stylesService.setStyleSheets(style, "test", "testDark", false, true);
        assertTrue(style.contains(STYLE_GLOBAL_DARK));
        assertTrue(style.contains("testDark"));
        stylesService.setStyleSheets(style, "test", "testDark", true, false);
        assertTrue(style.contains(STYLE_GLOBAL));
        assertTrue(style.contains("test"));
    }
}