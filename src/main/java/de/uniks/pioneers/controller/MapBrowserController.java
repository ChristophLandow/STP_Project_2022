package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.PrefService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class MapBrowserController implements Controller {
    @Inject
    Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Inject
    PrefService prefService;

    @FXML
    ListView mapListView;
    @FXML
    private final App app;
    private LobbyScreenController lobbyScreenController;
    @Inject
    public MapBrowserController(App app){
        this.app = app;
    }
    


    @Override
    public void init() {
        if(prefService.getDarkModeState()){
            app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/MapBrowser.css")));
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_MapBrowser.css");
        } else {
            app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_MapBrowser.css")));
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/MapBrowser.css");
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MapBrowserScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent mapBrowserView;
        try {
            mapBrowserView =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return mapBrowserView;
    }

    public void editMap(ActionEvent actionEvent) {
    }

    public void createNewMap(ActionEvent actionEvent) {
    }

    public void leaveToLobby(ActionEvent actionEvent) {
        lobbyScreenController = lobbyScreenControllerProvider.get();
        app.show(lobbyScreenController);
    }
}
