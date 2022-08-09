package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.DISCARD_NUMBER;

@Singleton
public class RobberController implements Controller {
    @Inject Provider<DiscardOrChooseResourcesController> discardOrChooseResourcesControllerProvider;
    @Inject Provider<RobPlayerController> robPlayerControllerProvider;
    @Inject GameService gameService;
    @Inject PrefService prefService;
    @Inject IngameService ingameService;
    private RobberService robberService;
    @Inject MapRenderService mapRenderService;
    @Inject SpeechService speechService;
    private DiscardOrChooseResourcesController discardOrChooseResourcesController;
    private RobPlayerController robPlayerController;
    private final ChangeListener<Number> changeListener = (observable, oldValue, newValue) -> callNext(newValue.intValue());

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public RobberController(){
    }

    @Override
    public void init() {
        robberService.setup();
        callNext(this.robberService.getRobberState().get());
        this.robberService.getRobberState().addListener(changeListener);
    }

    public void setRobberService(RobberService robberService){
        this.robberService = robberService;
    }

    public void callNext(int newValue) {
        if (newValue == GameConstants.ROBBER_DISCARD) {
            discardOrChoose(DISCARD_NUMBER);
        }
        else if (newValue == GameConstants.ROBBER_STEAL) {
            rob();
        }
        else if(newValue == GameConstants.ROBBER_FINISHED){
            stop();
        }
    }

    public void discardOrChoose(int state) {
        discardOrChooseResourcesController = discardOrChooseResourcesControllerProvider.get();
        discardOrChooseResourcesController.setState(state);
        discardOrChooseResourcesController.init();
    }

    public void rob(){
        if (robberService.getRobbingCandidates().size() != 0) {
            speechService.play(GameConstants.SPEECH_STEAL);
            robPlayerController = robPlayerControllerProvider.get();
            robPlayerController.init();
        }
        else{
            disposable.add(this.robberService.robPlayer(null).observeOn(FX_SCHEDULER).subscribe(move -> stop()));
        }
    }

    @Override
    public void stop() {
        if(robberService != null) {
            this.robberService.getRobberState().removeListener(changeListener);
        }

        if(discardOrChooseResourcesController != null){
            discardOrChooseResourcesController.stop();
        }

        if(robPlayerController != null){
            robPlayerController.stop();
        }
    }
    
    @Override
    public Parent render() {
        return null;
    }
}