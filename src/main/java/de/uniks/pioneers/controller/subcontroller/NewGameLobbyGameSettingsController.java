package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.List;

public class NewGameLobbyGameSettingsController implements Controller {
    private final MapBrowserService mapBrowserService;
    private Spinner<Integer> boardSizeSpinner, victoryPointSpinner;
    private ComboBox<Text> mapComboBox;

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

        // load maps into comboBox
        List<MapTemplate> maps = mapBrowserService.getMaps();
        for (MapTemplate map : maps) {
            Text element = new Text(map.name());
            element.setId(map._id());
            mapComboBox.getItems().add(element);
        }
        mapComboBox.setValue(new Text("Default"));
    }

    @Override
    public Parent render() {
        return null;
    }

    @Override
    public void stop() {

    }

    public String getMapTemplateID(){
        if (mapComboBox.getSelectionModel().getSelectedIndex() == 0) {
            // default map
            return null;
        }
        return mapComboBox.getSelectionModel().getSelectedItem().getId();
    }

    public int getMapSize(){
        if(mapComboBox.getSelectionModel().getSelectedIndex() == 0) {
            // get size for default map
            return boardSizeSpinner.getValueFactory().getValue();
        }
        return 0;
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

    public void setMapComboBox(ComboBox<Text> mapComboBox) {
        this.mapComboBox = mapComboBox;
    }

}
