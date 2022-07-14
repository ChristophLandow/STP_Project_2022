package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.services.PrefService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import static de.uniks.pioneers.Constants.*;

public class HotkeyController implements Controller, Initializable {

    @FXML
    public TextField tradingTextField;
    @FXML public TextField endTurnTextField;
    @FXML public TextField openRulesTextField;
    @FXML public TextField openSettingsTextField;
    @FXML public ChoiceBox<String> tradingChoiceBox;
    @FXML public ChoiceBox<String> endTurnChoiceBox;
    @FXML public ChoiceBox<String> openRulesChoiceBox;
    @FXML public ChoiceBox<String> openSettingsChoiceBox;
    @FXML public Text ShortcutsText;
    @FXML public Text tradingText;
    @FXML public Text endTurnText;
    @FXML public Text openSettingsText;
    @FXML public Text openRulesText;
    @FXML public Button safeButton;
    @FXML public Text identicText;
    private final PrefService prefService;
    private final IngameScreenController ingameController;
    private final String[] hotkeyChoiceBoxElements = {NOHOTKEY,STRG, ALT};
    private final ArrayList<ChoiceBox<String>> hotkeyChoiceBoxVariants = new ArrayList<>();
    private final ArrayList<TextField> hotkeyTextfieldVariants = new ArrayList<>();
    private final ArrayList<HotkeyEventController> hotkeyControllers = new ArrayList<>();
    private final Scene scene;
    public HotkeyController(Scene scene, PrefService prefService, IngameScreenController ingameController) {
        this.scene = scene;
        this.ingameController = ingameController;
        this.prefService = prefService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Collections.addAll(hotkeyChoiceBoxVariants, tradingChoiceBox,endTurnChoiceBox,openRulesChoiceBox,openSettingsChoiceBox);
        Collections.addAll(hotkeyTextfieldVariants, tradingTextField,endTurnTextField,openSettingsTextField,openRulesTextField);
        for (ChoiceBox<String> box : hotkeyChoiceBoxVariants){
            box.getItems().addAll(hotkeyChoiceBoxElements);
        }
        tradingChoiceBox.setValue(prefService.getTradeChoiceBox());
        endTurnChoiceBox.setValue(prefService.getEndChoiceBox());
        openRulesChoiceBox.setValue(prefService.getRulesChoiceBox());
        openSettingsChoiceBox.setValue(prefService.getSettingsChoiceBox());

        tradingTextField.setText(prefService.getTradeTextField().toString());
        endTurnTextField.setText(prefService.getEndTextField().toString());
        openRulesTextField.setText(prefService.getRulesTextField().toString());
        openSettingsTextField.setText(prefService.getSettingsTextField().toString());

    }

    @Override
    public void init() {
    }

    @Override
    public void stop() {
    }

    @Override
    public Parent render() {
        Parent parent = null;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/hotKeySettings.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    public KeyCode stringToKeyCode(Character letter){
        return switch (letter) {
            case 'a' -> KeyCode.A;
            case 'b' -> KeyCode.B;
            case 'c' -> KeyCode.C;
            case 'd' -> KeyCode.D;
            case 'e' -> KeyCode.E;
            case 'f' -> KeyCode.F;
            case 'g' -> KeyCode.G;
            case 'h' -> KeyCode.H;
            case 'i' -> KeyCode.I;
            case 'j' -> KeyCode.J;
            case 'k' -> KeyCode.K;
            case 'l' -> KeyCode.L;
            case 'm' -> KeyCode.M;
            case 'n' -> KeyCode.N;
            case 'o' -> KeyCode.O;
            case 'p' -> KeyCode.P;
            case 'q' -> KeyCode.Q;
            case 'r' -> KeyCode.R;
            case 's' -> KeyCode.S;
            case 't' -> KeyCode.T;
            case 'u' -> KeyCode.U;
            case 'v' -> KeyCode.V;
            case 'w' -> KeyCode.W;
            case 'x' -> KeyCode.X;
            case 'y' -> KeyCode.Y;
            case 'z' -> KeyCode.Z;
            case '0' -> KeyCode.DIGIT0;
            case '1' -> KeyCode.DIGIT1;
            case '2' -> KeyCode.DIGIT2;
            case '3' -> KeyCode.DIGIT3;
            case '4' -> KeyCode.DIGIT4;
            case '5' -> KeyCode.DIGIT5;
            case '6' -> KeyCode.DIGIT6;
            case '7' -> KeyCode.DIGIT7;
            case '8' -> KeyCode.DIGIT8;
            case '9' -> KeyCode.DIGIT9;
            case '-' -> KeyCode.MINUS;
            case ',' -> KeyCode.COMMA;
            case '.' -> KeyCode.PERIOD;
            case '+' -> KeyCode.PLUS;
            case '#' -> KeyCode.NUMBER_SIGN;
            case '<' -> KeyCode.LESS;
            default -> throw new IllegalArgumentException("Cannot convert character :" + letter);
        };
    }

    private void safeTradeHotkeys(){
        if(!tradingTextField.getText().equals("") && !tradingChoiceBox.getValue().equals("")){
            Character tradeChar = prefService.saveTradeTextInput(tradingTextField.getText()).charAt(0);
            HotkeyEventController tradeHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(tradeHotkeyController);
            if(prefService.saveTradeChoiceBox(tradingChoiceBox.getValue()).equals(STRG)){
                tradeHotkeyController.setHotkey(stringToKeyCode(tradeChar),STRG, TRADE);
            } else {
                tradeHotkeyController.setHotkey(stringToKeyCode(tradeChar),ALT, TRADE);
            }
        }
    }

    public void safeEndTurnHotKeys(){
        if(!endTurnTextField.getText().equals("") && !endTurnChoiceBox.getValue().equals("")){
            Character endChar = prefService.saveEndTextInput(endTurnTextField.getText()).charAt(0);
            HotkeyEventController endHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(endHotkeyController);
            if(prefService.saveEndChoiceBox(endTurnChoiceBox.getValue()).equals(STRG)){
                endHotkeyController.setHotkey(stringToKeyCode(endChar),STRG, END);
            } else {
                endHotkeyController.setHotkey(stringToKeyCode(endChar),ALT, END);
            }
        }
    }

    public void safeOpenSettingsHotKeys(){
        if(!openSettingsTextField.getText().equals("") && !openSettingsChoiceBox.getValue().equals("")){
            Character settingsChar = prefService.saveSettingsTextInput(openSettingsTextField.getText()).charAt(0);
            HotkeyEventController settingsHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(settingsHotkeyController);
            if(prefService.saveSettingsChoiceBox(openSettingsChoiceBox.getValue()).equals(STRG)){
                settingsHotkeyController.setHotkey(stringToKeyCode(settingsChar),STRG, SETTINGS);
            } else {
                settingsHotkeyController.setHotkey(stringToKeyCode(settingsChar),ALT, SETTINGS);
            }
        }
    }

    public void safeOpenRulesHotkeys(){
        if(!openRulesTextField.getText().equals("") && !openRulesChoiceBox.getValue().equals("")){
            Character rulesChar = prefService.saveRulesTextInput(openRulesTextField.getText()).charAt(0);
            HotkeyEventController rulesHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(rulesHotkeyController);
            if(prefService.saveRulesChoiceBox(openRulesChoiceBox.getValue()).equals(STRG)){
                rulesHotkeyController.setHotkey(stringToKeyCode(rulesChar),STRG, RULES);
            } else {
                rulesHotkeyController.setHotkey(stringToKeyCode(rulesChar),ALT, RULES);
            }
        }
    }

    public void safeHotkeys() {
        boolean equalHotkeys = false;
        for(TextField field : hotkeyTextfieldVariants){
            for(TextField field2 : hotkeyTextfieldVariants){
                if(field.equals(field2)){
                    continue;
                }
                if((field.getText().equals(field2.getText())) && !(field.getText().isEmpty() && field2.getText().isEmpty())){
                    equalHotkeys = true;
                }
            }
        }
        if(equalHotkeys){
            identicText.setText("Identical shortcuts exist!");
        } else {
            for(HotkeyEventController controller : hotkeyControllers){
                controller.stop();
            }
            identicText.setText("");
            hotkeyControllers.clear();
            safeTradeHotkeys();
            safeEndTurnHotKeys();
            safeOpenRulesHotkeys();
            safeOpenSettingsHotKeys();
        }
    }
}