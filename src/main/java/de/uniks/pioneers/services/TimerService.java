package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.BUILD;

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
                        .subscribe(move -> {
                            this.cancel();
                            reset();
                        })
                );
            }
        };

        this.timer.schedule(task, 10 * 1000);
    }

    public void reset() {
        this.timer.cancel();
    }

    public void setBuildTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(BUILD, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> {
                            System.out.println("Time is up! BUILD skipped.");
                            this.cancel();
                            reset();
                        })
                );
            }
        };

        this.timer.schedule(task, 120 * 1000);
    }

    public Timer getTimer() {
        return timer;
    }
}
