package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;


public class RobberController implements Controller {

    private final Provider<DiscardResourcesController> discardResourcesControllerProvider;
    @Inject
    GameService gameService;
    @Inject
    PrefService prefService;
    @Inject
    IngameService ingameService;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public RobberController(Provider<DiscardResourcesController> discardResourcesControllerProvider){
        this.discardResourcesControllerProvider = discardResourcesControllerProvider;
    }

    @Override
    public void init(){
        discard();

    }

    private void discard() {
        /*
        DiscardResourcesController discardController = discardResourcesControllerProvider.get();
        discardController.init();
        */
    }

    @Override
    public void stop() {
    }
    @Override
    public Parent render() {
        return null;
    }

    private void setRobber() {
    }
}