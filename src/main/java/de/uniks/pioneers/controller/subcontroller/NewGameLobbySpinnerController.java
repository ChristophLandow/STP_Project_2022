package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.MapBrowserService;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javax.inject.Inject;

public class NewGameLobbySpinnerController implements Controller {

    private Spinner<Integer> boardSizeSpinner, victoryPointSpinner;
    private Spinner<String> mapTemplateSpinner;

    private final MapBrowserService mapBrowserService;

    @Inject
    public NewGameLobbySpinnerController(MapBrowserService mapBrowserService) {
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
    }

    @Override
    public Parent render() {
        return null;
    }

    @Override
    public void stop() {

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
