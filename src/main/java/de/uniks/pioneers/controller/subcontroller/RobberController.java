package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class RobberController {

    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;
    private final HexTile tile;
    private String action;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public RobberController(HexTile tile, String action){
        this.tile = tile;
        this.action = action;
    }

    public void init(){
        discard();
        itsRobbingTime();
    }

    private void discard() {
    }

    public HexTile getTile(){
        return this.tile;
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
        rob();
    }

    private void rob(){
    }






}
