package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class RobberController implements Controller {
    @Inject Provider<DiscardResourcesController> discardResourcesControllerProvider;
    @Inject Provider<RobPlayerController> robPlayerControllerProvider;
    @Inject GameService gameService;
    @Inject PrefService prefService;
    @Inject IngameService ingameService;
    @Inject RobberService robberService;
    @Inject MapRenderService mapRenderService;

    private DiscardResourcesController discardResourcesController;
    private RobPlayerController robPlayerController;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public RobberController(){
    }

    @Override
    public void init() {
        robberService.setup();
        this.robberService.getRobberState().addListener((observable, oldValue, newValue) -> callNext(newValue.intValue()));
        discard();
    }

    public void callNext(int newValue) {
        if (newValue == GameConstants.ROBBER_STEAL) {
            rob();
        }
    }

    private void discard() {
        if(gameService.getRessourcesSize() >= 7) {
            discardResourcesController = discardResourcesControllerProvider.get();
            discardResourcesController.init();
        }
        else{
            this.robberService.getRobberState().set(GameConstants.ROBBER_MOVE);
        }
    }

    public void rob(){
        robberService.updateRobbingCandidates();

        if(robberService.getRobbingCandidates().size() != 0) {
            robPlayerController = robPlayerControllerProvider.get();
            robPlayerController.init();
        }
        else{
            disposable.add(this.robberService.robPlayer(null).observeOn(FX_SCHEDULER).subscribe(move -> {}));
        }
    }

    @Override
    public void stop() {
        this.robberService.getRobberState().set(GameConstants.ROBBER_DISCARD);

        if(discardResourcesController != null){
            discardResourcesController.stop();
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