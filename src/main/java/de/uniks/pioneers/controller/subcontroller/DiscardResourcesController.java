package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.RobberService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class DiscardResourcesController implements Initializable, Controller {
    @FXML private Text numeratorText;
    @FXML private Text denominatorText;
    @FXML private Spinner<Integer> CarbonSpinner;
    @FXML private Spinner<Integer> FishSpinner;
    @FXML private Spinner<Integer> PolarBearSpinner;
    @FXML private Spinner<Integer> IceSpinner;
    @FXML private Spinner<Integer> WaleSpinner;

    private final  Provider<RobberController> robberControllerProvider;
    private final Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;
    @Inject
    RobberService robberService;
    public Integer wale;
    public Integer ice;
    public Integer polarbear;
    public Integer fish;
    public Integer carbon;
    private Stage stage;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ArrayList<Spinner<Integer>> spinnerLIst = new ArrayList<>();

    @Inject
    public DiscardResourcesController(Provider<IngameScreenController> ingameScreenControllerProvider, Provider<RobberController> robberControllerProvider) {
        this.robberControllerProvider = robberControllerProvider;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;

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
        spinnerLIst.add(CarbonSpinner);
        SpinnerValueFactory<Integer> fishValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,fish);
        fishValueFactory.setValue(0);
        FishSpinner.setValueFactory(fishValueFactory);
        spinnerLIst.add(FishSpinner);
        SpinnerValueFactory<Integer> polarbearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,polarbear);
        polarbearValueFactory.setValue(0);
        PolarBearSpinner.setValueFactory(polarbearValueFactory);
        spinnerLIst.add(PolarBearSpinner);
        SpinnerValueFactory<Integer> iceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,ice);
        iceValueFactory.setValue(0);
        IceSpinner.setValueFactory(iceValueFactory);
        spinnerLIst.add(IceSpinner);
        SpinnerValueFactory<Integer> waleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,wale);
        waleValueFactory.setValue(0);
        WaleSpinner.setValueFactory(waleValueFactory);
        spinnerLIst.add(WaleSpinner);
        numeratorText.setText(Integer.toString(0));
        denominatorText.setText(Integer.toString((fish+wale+polarbear+ice+carbon)/2));
        for(Spinner<Integer> spinner : spinnerLIst){
            spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                int newNumerator = 0;
                for(Spinner<Integer> spinner1 : spinnerLIst){
                    newNumerator += spinner1.getValue();
                }
                numeratorText.setText(Integer.toString(newNumerator));
            });
        }
    }

    @Override
    public void init(){
        //set stage
        this.stage = new Stage();
        Parent node = render();
        this.stage.setTitle("Discard resource cards");
        Scene scene = new Scene(node);
        this.stage.setScene(scene);
        if(prefService.getDarkModeState()){
            scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
            scene.getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
        } else {
            scene.getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
            scene.getStylesheets().add("/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
        }
        this.stage.show();
    }

    @Override
    public void stop() {
        stage.close();
        spinnerLIst.clear();
        disposable.dispose();
    }

    public void discardAndLeave(){
        chooseResources();
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
//        IngameScreenController ingameController = ingameScreenControllerProvider.get();
        int walediscard = WaleSpinner.getValue();
        int iceDiscard = IceSpinner.getValue();
        int polarbearDeiscard = PolarBearSpinner.getValue();
        int fishDiscard = FishSpinner.getValue();
        int carbonDiscard = CarbonSpinner.getValue();

        disposable.add(robberService.dropRessources(new Resources(-walediscard, -iceDiscard, -carbonDiscard, -fishDiscard, -polarbearDeiscard))
                .observeOn(FX_SCHEDULER).take(1).subscribe(move -> {
                    robberService.getRobberState().set(ROBBER_MOVE);
                    stop();
                },this::handleHttpError));
    }

    private  void handleHttpError(Throwable exception) throws IOException {
        String errorBody;
        if (exception instanceof HttpException httpException) {
            errorBody = Objects.requireNonNull(Objects.requireNonNull(httpException.response()).errorBody()).string();
        } else {
            return;
        }

        System.out.println("!!!An Http Error appeared!!!\n" + errorBody);
    }

    public void show() {
    }
}
