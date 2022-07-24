package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.MapListController;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import static org.mockito.Mockito.when;

import javax.inject.Provider;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class MapBrowserControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock(name = "ingameScreenControllerProvider")
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @Mock(name = "mapListControllerProvider")
    Provider<MapListController> mapListControllerProvider;

    @Mock
    PrefService prefService;

    @Mock
    MapBrowserService mapBrowserService;

    @Mock
    StylesService stylesService;

    @InjectMocks
    IngameScreenController ingameScreenController;

    @InjectMocks
    MapListController mapListController;

    @InjectMocks MapBrowserController mapBrowserController;

    @Override
    public void start(Stage stage){
        when(mapListControllerProvider.get()).thenReturn(mapListController);

        ArrayList<MapTemplate> returnValue = new ArrayList<>();
        returnValue.add(new MapTemplate("","","","","","",0,null, null));
        when(mapBrowserService.getMaps()).thenReturn(FXCollections.observableArrayList(returnValue));

        app.start(stage);
        app.show(mapBrowserController);
    }

    @Test
    void test() {

    }
}
