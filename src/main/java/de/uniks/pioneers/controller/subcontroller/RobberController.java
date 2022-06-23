package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class RobberController implements Controller {

    @Inject
    Provider<DiscardResourcesController> discardResourcesControllerProvider;
    private final GameService gameService;

    private final PrefService prefService;

    private final IngameService ingameService;
    private String action;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public RobberController(Provider<DiscardResourcesController> discardResourcesControllerProvider
            , GameService gameService, PrefService prefService, IngameService ingameService){
        this.discardResourcesControllerProvider = discardResourcesControllerProvider;
        this.gameService = gameService;
        this.prefService = prefService;
        this.ingameService = ingameService;

    }

    @Override
    public void init(){
        discard();
        //itsRobbingTime();
    }

    private void discard() {
        DiscardResourcesController discardController = discardResourcesControllerProvider.get();

        Parent node = discardController.render();
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
        discardController.init();
        stage.show();
    }

    @Override
    public void stop() {
    }
    @Override
    public Parent render() {
        return null;
    }

    private void itsRobbingTime() {
        CreateMoveDto robMove = new CreateMoveDto(this.action, null, null);
        disposable.add(ingameService.postMove(gameService.game.get()._id(), robMove)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    setRobber();
                }));
    }

    private void setRobber() {
    }
}