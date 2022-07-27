package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.MapBrowserService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javax.inject.Inject;

public class NewGameLobbyGameSettingsController implements Controller {

    private Spinner<Integer> boardSizeSpinner, victoryPointSpinner;
    private Spinner<String> mapTemplateSpinner;
    private final MapBrowserService mapBrowserService;

    private int indexOfSpinner = -1;

    @Inject
    public NewGameLobbyGameSettingsController(MapBrowserService mapBrowserService) {
        this.mapBrowserService = mapBrowserService;
    }

    @Override
    public void init() {
        boardSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10));
        boardSizeSpinner.editorProperty().get().setAlignment(Pos.CENTER);
        boardSizeSpinner.getValueFactory().setValue(2);

        victoryPointSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3,15,10));
        victoryPointSpinner.editorProperty().get().setAlignment(Pos.CENTER);

        mapTemplateSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(mapBrowserService.getMapNames()));
        mapTemplateSpinner.editorProperty().get().setAlignment(Pos.CENTER);
        mapTemplateSpinner.getValueFactory().setValue("Default");

        Node buttonIncrement = mapTemplateSpinner.getChildrenUnmodifiable().get(1);
        buttonIncrement.setOnMouseClicked((event)-> {
            indexOfSpinner++;

            if(indexOfSpinner >= mapBrowserService.getMapNames().size()){
                indexOfSpinner = mapBrowserService.getMapNames().size()-1;
            }
        });

        Node buttonDecrement = mapTemplateSpinner.getChildrenUnmodifiable().get(2);
        buttonDecrement.setOnMouseClicked((event)-> {
            indexOfSpinner--;

            if(indexOfSpinner < -1){
                indexOfSpinner = -1;
            }
        });
    }

    @Override
    public Parent render() {
        return null;
    }

    @Override
    public void stop() {

    }

    public String getMapTemplateID(){
        if(indexOfSpinner == -1){
            return null;
        }
        else{
            return mapBrowserService.getMaps().get(indexOfSpinner)._id();
        }
    }

    public int getMapSize(){
        return boardSizeSpinner.getValueFactory().getValue();
    }

    public int getVictoryPoints(){
        return victoryPointSpinner.getValueFactory().getValue();
    }

    public void setBoardSizeSpinner(Spinner<Integer> boardSizeSpinner) {
        this.boardSizeSpinner = boardSizeSpinner;
    }

    public void setVictoryPointSpinner(Spinner<Integer> victoryPointSpinner) {
        this.victoryPointSpinner = victoryPointSpinner;
    }

    public void setMapTemplateSpinner(Spinner<String> mapTemplateSpinner) {
        this.mapTemplateSpinner = mapTemplateSpinner;
    }
}
