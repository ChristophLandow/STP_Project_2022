package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateMoveDto;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class DiscardOrChooseResourcesController implements Initializable, Controller {
    @FXML private Text numeratorText;
    @FXML private Text denominatorText;
    @FXML private Spinner<Integer> CarbonSpinner;
    @FXML private Spinner<Integer> FishSpinner;
    @FXML private Spinner<Integer> PolarBearSpinner;
    @FXML private Spinner<Integer> IceSpinner;
    @FXML private Spinner<Integer> WaleSpinner;
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
    public Integer polarBear;
    public Integer fish;
    public Integer carbon;
    private Stage stage;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ArrayList<Spinner<Integer>> spinnerList = new ArrayList<>();
    private int state;

    @Inject
    public DiscardOrChooseResourcesController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(state == DISCARD_NUMBER) {
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
                polarBear = ingameResources.wool();
            } else {
                polarBear = 0;
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

            if((fish + wale + polarBear + ice + carbon) < 8) {
                this.stop();
            }

            SpinnerValueFactory<Integer> carbonValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, carbon);
            carbonValueFactory.setValue(0);
            CarbonSpinner.setValueFactory(carbonValueFactory);
            spinnerList.add(CarbonSpinner);
            SpinnerValueFactory<Integer> fishValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, fish);
            fishValueFactory.setValue(0);
            FishSpinner.setValueFactory(fishValueFactory);
            spinnerList.add(FishSpinner);
            SpinnerValueFactory<Integer> polarbearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, polarBear);
            polarbearValueFactory.setValue(0);
            PolarBearSpinner.setValueFactory(polarbearValueFactory);
            spinnerList.add(PolarBearSpinner);
            SpinnerValueFactory<Integer> iceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, ice);
            iceValueFactory.setValue(0);
            IceSpinner.setValueFactory(iceValueFactory);
            spinnerList.add(IceSpinner);
            SpinnerValueFactory<Integer> waleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, wale);
            waleValueFactory.setValue(0);
            WaleSpinner.setValueFactory(waleValueFactory);
            spinnerList.add(WaleSpinner);
            numeratorText.setText(Integer.toString(0));
            denominatorText.setText(Integer.toString((fish + wale + polarBear + ice + carbon)/2));

            for(Spinner<Integer> spinner : spinnerList){
                spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int newNumerator = 0;
                    for(Spinner<Integer> spinner1 : spinnerList){
                        newNumerator += spinner1.getValue();
                    }
                    numeratorText.setText(Integer.toString(newNumerator));
                });
            }
        } else {
            SpinnerValueFactory<Integer> carbonValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, state);
            carbonValueFactory.setValue(0);
            CarbonSpinner.setValueFactory(carbonValueFactory);
            spinnerList.add(CarbonSpinner);
            SpinnerValueFactory<Integer> fishValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, state);
            fishValueFactory.setValue(0);
            FishSpinner.setValueFactory(fishValueFactory);
            spinnerList.add(FishSpinner);
            SpinnerValueFactory<Integer> polarbearValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, state);
            polarbearValueFactory.setValue(0);
            PolarBearSpinner.setValueFactory(polarbearValueFactory);
            spinnerList.add(PolarBearSpinner);
            SpinnerValueFactory<Integer> iceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, state);
            iceValueFactory.setValue(0);
            IceSpinner.setValueFactory(iceValueFactory);
            spinnerList.add(IceSpinner);
            SpinnerValueFactory<Integer> waleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, state);
            waleValueFactory.setValue(0);
            WaleSpinner.setValueFactory(waleValueFactory);
            spinnerList.add(WaleSpinner);
            numeratorText.setText(Integer.toString(0));
            denominatorText.setText(Integer.toString(state));

            for(Spinner<Integer> spinner : spinnerList){
                spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    int newNumerator = 0;
                    for(Spinner<Integer> spinner1 : spinnerList){
                        newNumerator += spinner1.getValue();
                    }
                    numeratorText.setText(Integer.toString(newNumerator));
                });
            }
        }
    }

    @Override
    public void init(){
        this.stage = new Stage();
        Parent node = render();

        if(state == DISCARD_NUMBER) {
            this.stage.setTitle("Discard resource cards");
        } else if (state == MONOPOLY_NUMBER){
            this.stage.setTitle("Choose a resource card");
        } else if (state == PLENTY_NUMBER){
            this.stage.setTitle("Choose two resource cards");
        }

        Scene scene = new Scene(node);
        this.stage.setScene(scene);
        this.stage.setOnCloseRequest(event -> event.consume());

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
        spinnerList.clear();
        disposable.dispose();
        state = -1;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/DiscardOrChooseResourcesPopup.fxml"));
        loader.setControllerFactory(c->this);
        final Parent discardView;
        try {
            discardView = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return discardView;
    }

    public void discardOrChoose(){
        int waleCount = WaleSpinner.getValue();
        int iceCount = IceSpinner.getValue();
        int polarBearCount = PolarBearSpinner.getValue();
        int fishCount = FishSpinner.getValue();
        int carbonCount = CarbonSpinner.getValue();
        int resourceCount = waleCount + iceCount + polarBearCount + fishCount + carbonCount;

        if(state == DISCARD_NUMBER) {
            if (resourceCount != ((fish + wale + polarBear + ice + carbon)/2)) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You are trying to discard more or less resources than necessary!");
                alert.showAndWait();
            } else {
                disposable.add(robberService.dropResources(new Resources(-waleCount, -iceCount, -carbonCount, -fishCount, -polarBearCount))
                        .observeOn(FX_SCHEDULER)
                        .take(1)
                        .subscribe(move -> stop(),this::handleHttpError));
                this.stop();
            }
        } else {
            if (resourceCount != state) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You are trying to choose more or less resources than necessary!");
                alert.showAndWait();
            } else if (state == MONOPOLY_NUMBER) {
                disposable.add(ingameService.postMove(ingameService.game.get()._id(), new CreateMoveDto(MONOPOLY_MOVE, new Resources((waleCount > 0) ? waleCount : null, (iceCount > 0) ? iceCount : null,
                                (carbonCount > 0) ? carbonCount : null, (fishCount > 0) ? fishCount : null, (polarBearCount > 0) ? polarBearCount : null)))
                        .observeOn(FX_SCHEDULER)
                        .take(1)
                        .subscribe(move -> stop(),this::handleHttpError));

                this.stop();
            } else if (state == PLENTY_NUMBER) {
                disposable.add(ingameService.postMove(ingameService.game.get()._id(), new CreateMoveDto(PLENTY_MOVE, new Resources(waleCount, iceCount, carbonCount, fishCount, polarBearCount)))
                        .observeOn(FX_SCHEDULER)
                        .take(1)
                        .subscribe(move -> stop(),this::handleHttpError));
                this.stop();
            }
        }
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

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
