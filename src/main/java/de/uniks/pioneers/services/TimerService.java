package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.scene.control.Label;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.BUILD;

@Singleton
public class TimerService {
    private boolean timeUp;
    private Timer timer;
    private Label timeLabel;
    private Timer countdownTimer;

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

    public void setRollTimer(String action, Timer timer) {
        this.timer = timer;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(action, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> {
                            this.cancel();
                            timer.cancel();
                            countdownTimer.cancel();
                        })
                );
            }
        };

        this.initCountdown(new Timer(), 10);
        timer.schedule(task, 10 * 1000);
    }

    public void setBuildTimer(Timer timer) {
        this.timer = timer;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(BUILD, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> {
                            this.cancel();
                            timer.cancel();
                            countdownTimer.cancel();
                        })
                );
            }
        };
        this.initCountdown(new Timer(), 120);
        timer.schedule(task, 120 * 1000);
    }

    private void initCountdown(Timer timer, int sec) {
        this.countdownTimer = timer;
        TimerTask task = new TimerTask() {
            int remainingTime = sec;
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(String.valueOf(remainingTime--)));
            }
        };

        this.countdownTimer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void reset() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timeLabel.setText("");
        if (this.countdownTimer != null) {
            this.countdownTimer.cancel();
        }
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }
}
