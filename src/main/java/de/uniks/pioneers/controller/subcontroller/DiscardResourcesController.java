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

    @Inject Provider<RobberController> robberControllerProvider;
    private final IngameService ingameService;
    private final GameService gameService;
    private final PrefService prefService;
    public Integer wale;
    public Integer ice;
    public Integer polarbear;
    public Integer fish;
    public Integer carbon;



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
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public DiscardResourcesController(Provider<RobberController> robberControllerProvider, GameService gameService, PrefService prefService, IngameService ingameService) {
        this.robberControllerProvider = robberControllerProvider;
        this.gameService = gameService;
        this.prefService = prefService;
        this.ingameService = ingameService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        stage = (Stage) anchorPane.getScene().getWindow();
        Resources resources = gameService.players.get(gameService.me).resources();
        fish = resources.lumber();
        wale = resources.grain();
        carbon = resources.wool();
        ice = resources.brick();
        polarbear = resources.ore();
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
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return node;
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
