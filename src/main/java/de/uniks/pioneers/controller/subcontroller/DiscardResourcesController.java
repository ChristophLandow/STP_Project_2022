package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscardResourcesController implements Initializable {
    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;

    @FXML private AnchorPane anchorPane;
    @FXML private Text XfromSixText;
    @FXML private Text SlashText;
    @FXML private Text SixText;
    @FXML private Spinner<Integer> CarbonSpinner;
    @FXML private Spinner<Integer> FishSpinner;
    @FXML private Spinner<Integer> PolarBearSpinner;
    @FXML private Spinner<Integer> IceSpinner;
    @FXML private Spinner<Integer> WaleSpinner;
    private Stage stage;

    public DiscardResourcesController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> carbonValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100);
        carbonValueFactory.setValue(1);
        CarbonSpinner.setValueFactory(carbonValueFactory);
        SpinnerValueFactory<Integer> fishValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100);
        fishValueFactory.setValue(1);
        FishSpinner.setValueFactory(fishValueFactory);
        SpinnerValueFactory<Integer> polarbearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100);
        polarbearValueFactory.setValue(1);
        PolarBearSpinner.setValueFactory(polarbearValueFactory);
        SpinnerValueFactory<Integer> iceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100);
        iceValueFactory.setValue(1);
        IceSpinner.setValueFactory(iceValueFactory);
        SpinnerValueFactory<Integer> waleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100);
        waleValueFactory.setValue(1);
        WaleSpinner.setValueFactory(waleValueFactory);

    }


    public void init() {
        stage = (Stage) anchorPane.getScene().getWindow();
    }


    public void discardAndLeave(){
        stage.close();
    }

    public void render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/DiscardResourcesPopup.fxml"));
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = new Stage();
        stage.setTitle("Discard resource cards");
        if(node != null){
            Scene scene = new Scene(node);
            stage.setScene(scene);
//            if(prefService.getDarkModeState()){
//                scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
//                scene.getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
//            } else {
//                scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
//                scene.getStylesheets().add("/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
//            }
            stage.show();
            //Sollte das Fenster ohne auswahl geschlossen werden:
            Window popup = stage;
            popup.setOnCloseRequest(event -> render());
        }
    }

    public void chooseResources(){

    }
}
