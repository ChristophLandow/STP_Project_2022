package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Parent;
import javax.inject.Inject;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class RobberController implements Controller {
    private final GameService gameService;

    private final PrefService prefService;

    private final IngameService ingameService;
    private String action;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public RobberController(GameService gameService, PrefService prefService, IngameService ingameService){
        this.gameService = gameService;
        this.prefService = prefService;
        this.ingameService = ingameService;
        discard();
    }

    @Override
    public void init(){
        discard();
        //itsRobbingTime();
    }

    private void discard() {
        DiscardResourcesController discardController = new DiscardResourcesController();
        discardController.render();
        discardController.init();
        System.out.println(gameService.me);
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