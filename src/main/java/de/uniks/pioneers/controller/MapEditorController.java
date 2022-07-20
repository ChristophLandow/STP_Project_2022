package de.uniks.pioneers.controller;


import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javax.inject.Inject;
import java.io.IOException;


public class MapEditorController implements Controller{

    @FXML
    Button buttonToMaps;
    @FXML
    Button buttonSave;


    @Inject
    public MapEditorController(){}

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MapEditorScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent parent;
        try {
            parent =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void toMaps(){
    }
    public void save(){}


}
