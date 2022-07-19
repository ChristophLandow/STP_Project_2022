package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class MapBrowserController implements Controller {

    @Override
    public void init() {

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
    }
}
