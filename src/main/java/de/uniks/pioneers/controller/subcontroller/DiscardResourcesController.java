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
import javafx.scene.control.Spinner;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.IOException;

public class DiscardResourcesController implements Controller {
    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;
    @FXML Text XfromSixText;
    @FXML Text SlashText;
    @FXML Text SixText;
    @FXML Spinner<Integer> CarbonSpinner;
    @FXML Spinner<Integer> FishSpinner;
    @FXML Spinner<Integer> PolarBearSpinner;
    @FXML Spinner<Integer> IceSpinner;
    @FXML Spinner<Integer> WaleSpinner;
    private App app;

    @Override
    public void init() {
        if(prefService.getDarkModeState()){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
        }
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
}
