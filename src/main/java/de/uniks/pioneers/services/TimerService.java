package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class TimerService {
    private boolean timeUp;
    private final Timer timer = new Timer();

    private final IngameService ingameService;
    private final GameService gameService;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public TimerService(IngameService ingameService, GameService gameService) {
        this.ingameService = ingameService;
        this.gameService = gameService;
    }

    public boolean timeUp() {
        return timeUp;
    }

    public void setRollTimer(String action) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(action, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> reset())
                );
            }
        };

        this.timer.schedule(task, 10 * 1000);
    }

    public void reset() {
        this.timer.cancel();
    }

    public void setBuildTimer() {
    }

    public Timer getTimer() {
        return timer;
    }
}
