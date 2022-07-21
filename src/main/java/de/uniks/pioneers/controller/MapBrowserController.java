package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.StylesService;
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
    private final StylesService stylesService;
    @Inject
    public MapBrowserController(App app, StylesService stylesService){
        this.app = app;
        this.stylesService = stylesService;
    }
    


    @Override
    public void init() {
        String styleLocal = "/de/uniks/pioneers/styles/MapBrowser.css";
        String styleLocalDark = "/de/uniks/pioneers/styles/DarkMode_MapBrowser.css";
        stylesService.setStyleSheets(app.getStage().getScene().getStylesheets(), styleLocal, styleLocalDark);
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
    public App getApp() {
        return this.app;
    }
}