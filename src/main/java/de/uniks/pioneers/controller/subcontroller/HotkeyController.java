package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.PrefService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

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
    @Inject
    PrefService prefService;

    private final String[] hotkeyChoiceBoxElements = {"STRG", "ALT"};
    private final ArrayList<ChoiceBox<String>> hotkeyChoiceBoxVariants = new ArrayList<>();
    private final ArrayList<TextField> hotkeyTextFieldVariants = new ArrayList<>();
    private final Scene scene;
    public HBox hotkeyHBox;


    public HotkeyController(Scene scene) {
        this.scene = scene;
    }

    public void setKey(){
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ESCAPE,
                    KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    System.out.println("Key Pressed: " + keyComb);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //hotkeys
        Collections.addAll(hotkeyChoiceBoxVariants, tradingChoiceBox,endTurnChoiceBox,openRulesChoiceBox,openSettingsChoiceBox);
        Collections.addAll(hotkeyTextFieldVariants, tradingTextField,endTurnTextField,openRulesTextField,openSettingsTextField);
        for (ChoiceBox<String> box : hotkeyChoiceBoxVariants){
            box.getItems().addAll(hotkeyChoiceBoxElements);
        }
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
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/hotkeySettings.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return parent;
    }

    public void setHBOx(HBox hotkeyHBox) {
        this.hotkeyHBox = hotkeyHBox;
    }
}
