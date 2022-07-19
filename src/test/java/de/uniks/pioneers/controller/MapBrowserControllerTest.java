package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.services.PrefService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

@ExtendWith(MockitoExtension.class)
public class MapBrowserControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock(name = "ingameScreenControllerProvider")
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @Mock
    PrefService prefService;

    @InjectMocks
    IngameScreenController ingameScreenController;

    @InjectMocks MapBrowserController mapBrowserController;

    @Override
    public void start(Stage stage){
        app.start(stage);
        app.show(mapBrowserController);
    }

    @Test
    void test() {

    }
}
