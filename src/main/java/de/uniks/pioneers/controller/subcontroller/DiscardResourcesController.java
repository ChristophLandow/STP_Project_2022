package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import java.io.IOException;

public class DiscardResourcesController implements Controller {
    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;

    @FXML AnchorPane anchorPane;
    @FXML Text XfromSixText;
    @FXML Text SlashText;
    @FXML Text SixText;
    @FXML Spinner<Integer> CarbonSpinner;
    @FXML Spinner<Integer> FishSpinner;
    @FXML Spinner<Integer> PolarBearSpinner;
    @FXML Spinner<Integer> IceSpinner;
    @FXML Spinner<Integer> WaleSpinner;
    @FXML Button DiscardResourcesOKButton;
    private App app;

    private Stage stage;

    @Override
    public void init() {
        if(prefService.getDarkModeState()){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
        }
        // create stage and set window on close request
        stage = (Stage) anchorPane.getScene().getWindow();
        Window window = stage;
        //Soll den Räuber erneut auslösen, wenn man das Fenster einfach wegklickt!
        //window.setOnCloseRequest(event -> ingameScreenController.itsRobberyTime;
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/DiscardResources.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void discardAndLeave(){

    }
}
