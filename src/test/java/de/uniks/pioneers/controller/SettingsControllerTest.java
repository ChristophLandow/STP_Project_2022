package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;


import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javax.inject.Provider;



@ExtendWith(MockitoExtension.class)
public class SettingsControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    IngameScreenController ingameScreenController;

    @Mock(name = "ingameScreenControllerProvider")
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @InjectMocks
    SettingsScreenController settingsScreenController;


    @Override
    public void start(Stage stage) {
        app.start(stage);
        app.show(settingsScreenController);
    }

    @Test
    public void testDarkmode(){


    }

    @Test
    public void testImages(){
        FxAssert.verifyThat("#musicNote_ImageView", NodeMatchers.isVisible());
        FxAssert.verifyThat("#soundImageView", NodeMatchers.isVisible());
    }
}
