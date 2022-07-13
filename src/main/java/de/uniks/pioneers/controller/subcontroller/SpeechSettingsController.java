package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.PrefService;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

import javax.inject.Inject;

public class SpeechSettingsController implements Controller {

    private CheckBox voiceOutputCheckBox;
    private ChoiceBox<String> genderChoiceBox;

    final PrefService prefService;

    @Inject
    public SpeechSettingsController(PrefService prefService){
        this.prefService = prefService;
    }

    @Override
    public Parent render() {
        return null;
    }

    @Override
    public void init() {
        genderChoiceBox.setItems(FXCollections.observableArrayList("female voice", "male voice"));

        voiceOutputCheckBox.setSelected(prefService.getVoiceOutputActive());

        if(prefService.getGenderVoice().equals(GameConstants.FEMALE)){
            genderChoiceBox.getSelectionModel().select(0);
        }
        else{
            genderChoiceBox.getSelectionModel().select(1);
        }
    }

    @Override
    public void stop() {

    }

    public void setVoiceOutputCheckBox(CheckBox voiceOutputCheckBox) {
        this.voiceOutputCheckBox = voiceOutputCheckBox;
    }

    public void setGenderChoiceBox(ChoiceBox<String> genderChoiceBox) {
        this.genderChoiceBox = genderChoiceBox;
    }

    public void saveSettings(){
        prefService.saveVoiceOutputActive(voiceOutputCheckBox.isSelected());

        if(genderChoiceBox.getSelectionModel().getSelectedIndex() == 0){
            prefService.saveGenderVoice(GameConstants.FEMALE);
        }
        else{
            prefService.saveGenderVoice(GameConstants.MALE);
        }
    }
}
