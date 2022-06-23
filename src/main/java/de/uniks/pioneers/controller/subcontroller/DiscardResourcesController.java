package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscardResourcesController implements Initializable, Controller {

    @FXML private AnchorPane anchorPane;
    @FXML private Text XfromSixText;
    @FXML private Text SlashText;
    @FXML private Text SixText;
    @FXML private Spinner<Integer> CarbonSpinner;
    @FXML private Spinner<Integer> FishSpinner;
    @FXML private Spinner<Integer> PolarBearSpinner;
    @FXML private Spinner<Integer> IceSpinner;
    @FXML private Spinner<Integer> WaleSpinner;

    private final  Provider<RobberController> robberControllerProvider;

    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;
    public Integer wale;
    public Integer ice;
    public Integer polarbear;
    public Integer fish;
    public Integer carbon;
    private Stage stage;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public DiscardResourcesController(Provider<RobberController> robberControllerProvider, GameService gameService, PrefService prefService, IngameService ingameService) {
        this.robberControllerProvider = robberControllerProvider;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //load Resources
        Resources ingameResources = gameService.players.get(gameService.me).resources();
        if(ingameResources.lumber() != null){
            fish = ingameResources.lumber();
        } else {
            fish = 0;
        }
        if(ingameResources.grain() != null){
            wale = ingameResources.grain();
        } else {
            wale = 0;
        }
        if(ingameResources.wool() != null){
            polarbear = ingameResources.wool();
        } else {
            polarbear = 0;
        }
        if(ingameResources.brick() != null){
            ice = ingameResources.brick();
        } else {
            ice = 0;
        }
        if(ingameResources.ore() != null){
            carbon = ingameResources.ore();
        } else {
            carbon = 0;
        }
        SpinnerValueFactory<Integer> carbonValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, carbon);
        carbonValueFactory.setValue(0);
        CarbonSpinner.setValueFactory(carbonValueFactory);
        SpinnerValueFactory<Integer> fishValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,fish);
        fishValueFactory.setValue(0);
        FishSpinner.setValueFactory(fishValueFactory);
        SpinnerValueFactory<Integer> polarbearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,polarbear);
        polarbearValueFactory.setValue(0);
        PolarBearSpinner.setValueFactory(polarbearValueFactory);
        SpinnerValueFactory<Integer> iceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,ice);
        iceValueFactory.setValue(0);
        IceSpinner.setValueFactory(iceValueFactory);
        SpinnerValueFactory<Integer> waleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,wale);
        waleValueFactory.setValue(0);
        WaleSpinner.setValueFactory(waleValueFactory);
    }

    @Override
    public void init(){
        //set stage

        this.stage = new Stage();
        Parent node = render();
        Stage stage = new Stage();
        stage.setTitle("Discard resource cards");
        Scene scene = new Scene(node);
        stage.setScene(scene);
        if(prefService.getDarkModeState()){
            scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
            scene.getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
        } else {
            scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
            scene.getStylesheets().add("/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
        }
        stage.show();
    }

    @Override
    public void stop() {

    }

    public void discardAndLeave(){
        chooseResources();
        stage.close();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/DiscardResourcesPopup.fxml"));
        loader.setControllerFactory(c->this);
        final Parent discardView;
        try {
            discardView = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return discardView;
    }

    public void chooseResources(){
        int walediscard = WaleSpinner.getValue();
        int iceDiscard = IceSpinner.getValue();
        int polarbearDeiscard = PolarBearSpinner.getValue();
        int fishDiscard = FishSpinner.getValue();
        int carbonDiscard = CarbonSpinner.getValue();

    }

    public void show() {
    }
}
