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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import static de.uniks.pioneers.Constants.*;

public class HotkeyController implements Controller, Initializable {

    @FXML public TextField tradingTextField;
    @FXML public TextField endTurnTextField;
    @FXML public TextField openRulesTextField;
    @FXML public TextField openSettingsTextField;
    @FXML public TextField buildIglooTextField;
    @FXML public TextField buildStreetTextField;
    @FXML public TextField upgradeIglooTextField;
    @FXML public ChoiceBox<String> tradingChoiceBox;
    @FXML public ChoiceBox<String> endTurnChoiceBox;
    @FXML public ChoiceBox<String> openRulesChoiceBox;
    @FXML public ChoiceBox<String> openSettingsChoiceBox;
    @FXML public ChoiceBox<String> buildIglooChoiceBox;
    @FXML public ChoiceBox<String> buildStreetChoiceBox;
    @FXML public ChoiceBox<String> upgradeIglooChoiceBox;
    @FXML public Text ShortcutsText;
    @FXML public Text tradingText;
    @FXML public Text endTurnText;
    @FXML public Text openSettingsText;
    @FXML public Text openRulesText;
    @FXML public Text identicText;
    @FXML public Text buildStreetText;
    @FXML public Text upgradeIglooText;
    @FXML private Text buildIglooText;
    private final PrefService prefService;
    private final IngameScreenController ingameController;
    private final String[] hotkeyChoiceBoxElements = {NOHOTKEY,STRG, ALT};
    private final ArrayList<ChoiceBox<String>> hotkeyChoiceBoxVariants = new ArrayList<>();
    private final ArrayList<HotkeyEventController> hotkeyControllers = new ArrayList<>();
    public HotkeyEventController tradeHotkeyController;
    public HotkeyEventController endTurnHotkeyController;
    public HotkeyEventController openSettingsHotkeyController;
    public HotkeyEventController openRulesHotkeyController;
    public HotkeyEventController buildStreetHotkeyController;
    public HotkeyEventController buildIglooHotkeyController;
    public HotkeyEventController upgradeIglooHotkeyController;
    public Scene scene;

    public HotkeyController(Scene scene, PrefService prefService, IngameScreenController ingameController) {
        this.scene = scene;
        this.ingameController = ingameController;
        this.prefService = prefService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Collections.addAll(hotkeyChoiceBoxVariants,upgradeIglooChoiceBox,buildIglooChoiceBox,buildStreetChoiceBox,tradingChoiceBox,endTurnChoiceBox,openRulesChoiceBox,openSettingsChoiceBox);
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
        buildIglooChoiceBox.setValue(prefService.getBuildIglooChoiceBox());
        buildIglooTextField.setText(prefService.getBuildIglooTextField().toString());
        upgradeIglooChoiceBox.setValue(prefService.getUpgradeIglooChoiceBox());
        upgradeIglooTextField.setText(prefService.getUpgradeIglooTextField().toString());
        buildStreetChoiceBox.setValue(prefService.getBuildStreetChoiceBox());
        buildIglooTextField.setText(prefService.getBuildStreetTextField().toString());
        saveHotkeys();
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

    private void saveBuildStreetHotkey(){
        if(buildStreetTextField.getText().equals("") || buildStreetChoiceBox.getValue().equals("")){
            buildStreetChoiceBox.setValue("");
            buildStreetTextField.setText("");
            prefService.deleteBuildStreetHotkey();
        }
        if(!buildStreetTextField.getText().equals("") && !buildStreetChoiceBox.getValue().equals("")){
            Character buildStreetChar = prefService.saveBuildStreetTextInput(buildStreetTextField.getText()).charAt(0);
            buildStreetHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(buildStreetHotkeyController);
            if(prefService.saveBuildStreetChoiceBox(buildStreetChoiceBox.getValue()).equals(STRG)){
                buildStreetHotkeyController.setHotkey(stringToKeyCode(buildStreetChar),STRG, BUILDSTREET);
            } else {
                buildStreetHotkeyController.setHotkey(stringToKeyCode(buildStreetChar),ALT, BUILDSTREET);
            }
        }
    }

    private void saveBuildIglooHotkey(){
        if(buildIglooTextField.getText().equals("") || buildIglooChoiceBox.getValue().equals("")){
            buildIglooChoiceBox.setValue("");
            buildIglooTextField.setText("");
            prefService.deleteBuildIglooHotkey();
        }
        if(!buildIglooTextField.getText().equals("") && !buildIglooChoiceBox.getValue().equals("")){
            Character buildIgluChar = prefService.saveBuildIglooTextInput(buildIglooTextField.getText()).charAt(0);
            buildIglooHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(buildIglooHotkeyController);
            if(prefService.saveBuildIglooChoiceBox(buildIglooChoiceBox.getValue()).equals(STRG)){
                buildIglooHotkeyController.setHotkey(stringToKeyCode(buildIgluChar),STRG, BUILDIGLOO);
            } else {
                buildIglooHotkeyController.setHotkey(stringToKeyCode(buildIgluChar),ALT, BUILDIGLOO);
            }
        }
    }

    private void upgradeIglooHotkey(){
        if(upgradeIglooTextField.getText().equals("") || upgradeIglooChoiceBox.getValue().equals("")){
            upgradeIglooChoiceBox.setValue("");
            upgradeIglooTextField.setText("");
            prefService.deleteUpgradeIglooHotkey();
        }
        if(!upgradeIglooTextField.getText().equals("") && !upgradeIglooChoiceBox.getValue().equals("")){
            Character upgradeIglooChar = prefService.saveUpgradeIglooTextInput(upgradeIglooTextField.getText()).charAt(0);
            upgradeIglooHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(upgradeIglooHotkeyController);
            if(prefService.saveUpgradeIglooChoiceBox(upgradeIglooChoiceBox.getValue()).equals(STRG)){
                upgradeIglooHotkeyController.setHotkey(stringToKeyCode(upgradeIglooChar),STRG, UPGRADEIGLOO);
            } else {
                upgradeIglooHotkeyController.setHotkey(stringToKeyCode(upgradeIglooChar),ALT, UPGRADEIGLOO);
            }
        }
    }

    private void saveTradeHotkeys(){
        if(tradingTextField.getText().equals("") || tradingChoiceBox.getValue().equals("")){
            tradingChoiceBox.setValue("");
            tradingTextField.setText("");
            prefService.deleteTradeHotkey();
        }
        if(!tradingTextField.getText().equals("") && !tradingChoiceBox.getValue().equals("")){
            Character tradeChar = prefService.saveTradeTextInput(tradingTextField.getText()).charAt(0);
            tradeHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(tradeHotkeyController);
            if(prefService.saveTradeChoiceBox(tradingChoiceBox.getValue()).equals(STRG)){
                tradeHotkeyController.setHotkey(stringToKeyCode(tradeChar),STRG, TRADE);
            } else {
                tradeHotkeyController.setHotkey(stringToKeyCode(tradeChar),ALT, TRADE);
            }
        }
    }

    public void saveEndTurnHotKeys(){
        if(endTurnTextField.getText().equals("") || endTurnChoiceBox.getValue().equals("")){
            endTurnTextField.setText("");
            endTurnChoiceBox.setValue("");
            prefService.deleteEndHotkey();
        }
        if(!endTurnTextField.getText().equals("") && !endTurnChoiceBox.getValue().equals("")){
            Character endChar = prefService.saveEndTextInput(endTurnTextField.getText()).charAt(0);
            endTurnHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(endTurnHotkeyController);
            if(prefService.saveEndChoiceBox(endTurnChoiceBox.getValue()).equals(STRG)){
                endTurnHotkeyController.setHotkey(stringToKeyCode(endChar),STRG, END);
            } else {
                endTurnHotkeyController.setHotkey(stringToKeyCode(endChar),ALT, END);
            }
        }
    }

    public void saveOpenSettingsHotKeys(){
        if(openSettingsTextField.getText().equals("") || openSettingsChoiceBox.getValue().equals("")){
            openSettingsTextField.setText("");
            openSettingsChoiceBox.setValue("");
            prefService.deleteSettingsHotkey();
        }
        if(!openSettingsTextField.getText().equals("") && !openSettingsChoiceBox.getValue().equals("")){
            Character settingsChar = prefService.saveSettingsTextInput(openSettingsTextField.getText()).charAt(0);
            openSettingsHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(openSettingsHotkeyController);
            if(prefService.saveSettingsChoiceBox(openSettingsChoiceBox.getValue()).equals(STRG)){
                openSettingsHotkeyController.setHotkey(stringToKeyCode(settingsChar),STRG, SETTINGS);
            } else {
                openSettingsHotkeyController.setHotkey(stringToKeyCode(settingsChar),ALT, SETTINGS);
            }
        }
    }

    public void saveOpenRulesHotkeys(){
        if(openRulesTextField.getText().equals("") || openRulesChoiceBox.getValue().equals("")){
            openRulesChoiceBox.setValue("");
            openRulesTextField.setText("");
            prefService.deleteRulesHotkey();
        }
        if(!openRulesTextField.getText().equals("") && !openRulesChoiceBox.getValue().equals("")){
            Character rulesChar = prefService.saveRulesTextInput(openRulesTextField.getText()).charAt(0);
            openRulesHotkeyController = new HotkeyEventController(scene,ingameController);
            hotkeyControllers.add(openRulesHotkeyController);
            if(prefService.saveRulesChoiceBox(openRulesChoiceBox.getValue()).equals(STRG)){
                openRulesHotkeyController.setHotkey(stringToKeyCode(rulesChar),STRG, RULES);
            } else {
                openRulesHotkeyController.setHotkey(stringToKeyCode(rulesChar),ALT, RULES);
            }
        }
    }

    public void saveHotkeys() {
        boolean equalHotkeys = false;
        tradeHotkeyController = null;
        endTurnHotkeyController = null;
        openSettingsHotkeyController = null;
        openRulesHotkeyController = null;
        ArrayList<String> hotkeyVariants = new ArrayList<>();
        String tradeKeycomb = tradingChoiceBox.getValue() + tradingTextField.getText();
        String endKeycomb = endTurnChoiceBox.getValue() + endTurnTextField.getText();
        String rulesKeycomb = openRulesChoiceBox.getValue() + openRulesTextField.getText();
        String settingsKeyComb = openSettingsChoiceBox.getValue() + openSettingsTextField.getText();
        Collections.addAll(hotkeyVariants, tradeKeycomb,endKeycomb,rulesKeycomb,settingsKeyComb);
        for(String variant : hotkeyVariants){
            for(String varaint2 : hotkeyVariants){
                // == is used to check the location and not the content
                if((variant == varaint2) || ((variant.equals("")) && (varaint2.equals("")))){
                    continue;
                }
                //....as intended
                if (variant.equals(varaint2)) {
                    equalHotkeys = true;
                    break;
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
            saveTradeHotkeys();
            saveEndTurnHotKeys();
            saveOpenRulesHotkeys();
            saveOpenSettingsHotKeys();
            saveBuildStreetHotkey();
            saveBuildIglooHotkey();
            upgradeIglooHotkey();
        }
    }
}