package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.IngameDevelopmentCardController;
import de.uniks.pioneers.controller.subcontroller.IngameSelectController;
import de.uniks.pioneers.dto.CreateMoveDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.BUILD;

@Singleton
public class TimerService {
    private boolean timeUp = false;
    private Timer buildTimer;
    private Timer tradeTimer;
    private Label timeLabel;
    private Label tradeTimeLabel;
    private Timer countdownTimer;
    private Timer tradeCountdownTimer;
    private long remainingTime;
    private long remainingTurnTime;
    private int remainingTradeTime;
    private final IngameService ingameService;
    private final GameService gameService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private TimerTask countdownTimerTask;
    private TimerTask buildTimerTask;
    private TimerTask tradeCountdownTimerTask;
    private IngameSelectController ingameSelectController;
    private IngameDevelopmentCardController ingameDevelopmentCardController;

    @Inject
    public TimerService(IngameService ingameService, GameService gameService) {
        this.ingameService = ingameService;
        this.gameService = gameService;
    }

    public void init(IngameSelectController ingameSelectController, IngameDevelopmentCardController ingameDevelopmentCardController) {

        this.ingameSelectController = ingameSelectController;
        this.ingameDevelopmentCardController = ingameDevelopmentCardController;
    }

    public void setRollTimer(String action, Timer timer) {
        this.buildTimer = timer;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(action, null, null, null, null, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> {
                            this.cancel();
                            reset();
                        })
                );
            }
        };
        this.initCountdown(new Timer(), 10);
        timer.schedule(task, 10 * 1000);
    }

    public void setBuildTimer(Timer timer) {
        this.timeUp = false;
        this.buildTimer = timer;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CreateMoveDto moveDto = new CreateMoveDto(BUILD, null, null, null, null, null);
                disposable.add(ingameService.postMove(gameService.game.get()._id(), moveDto)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(move -> {
                            timeUp = true;
                            ingameSelectController.resetSelect();
                            ingameDevelopmentCardController.resetHammerSelection();
                            ingameDevelopmentCardController.closeDevCardPlayStage();
                            this.cancel();
                            reset();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Attention, your time has expired! \n" +
                                    "Your turn was automatically ended.");
                            alert.show();
                        })
                );
            }
        };
        this.buildTimerTask = task;
        if(remainingTurnTime < 0) {
            this.initCountdown(new Timer(), 120);
            timer.schedule(task, 120 * 1000);
        } else {
            if(remainingTurnTime < 10) {
                remainingTurnTime += 10;
            }
            this.initCountdown(new Timer(), remainingTurnTime);
            timer.schedule(task, remainingTurnTime * 1000);
        }
    }

    public void setTradeTimer(Timer timer) {
        int tradeTime = 30;
        this.tradeTimer = timer;
        remainingTurnTime = this.remainingTime;
        countdownTimerTask.cancel();
        countdownTimer.cancel();
        buildTimerTask.cancel();
        buildTimer.cancel();
        this.initTradeCountdown(new Timer(), tradeTime);
    }

    public void resetTradeTimer() {
        tradeCountdownTimerTask.cancel();
        tradeCountdownTimer.cancel();
        tradeTimer.cancel();
    }

    public void interruptBuildTimer() {
        remainingTurnTime = this.remainingTime;
        countdownTimerTask.cancel();
        countdownTimer.cancel();
        buildTimerTask.cancel();
        buildTimer.cancel();
    }

    private void initTradeCountdown(Timer timer, int sec) {
        this.remainingTradeTime = sec;
        this.tradeCountdownTimer = timer;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> tradeTimeLabel.setText(String.valueOf(remainingTradeTime--)));
            }
        };
        this.tradeCountdownTimerTask = task;
        this.tradeCountdownTimer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void initCountdown(Timer timer, long sec) {
        this.countdownTimer = timer;
        this.remainingTime = sec;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timeLabel.setText(String.valueOf(remainingTime--)));
            }
        };
        if (countdownTimerTask != null) {
            countdownTimerTask.cancel();
        }
        this.countdownTimerTask = task;
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void reset() {
        if (this.buildTimer != null) {
            this.buildTimer.cancel();
        }
        if(timeLabel != null) {
            this.timeLabel.setText("");
        }
        if (this.countdownTimer != null) {
            this.countdownTimer.cancel();
        }
        remainingTurnTime = -1;
    }

    public void stopTrade() {
        setBuildTimer(new Timer());
        // ingameService.declineTrade();
        this.tradeTimer.cancel();
        this.tradeCountdownTimerTask.cancel();
        this.tradeCountdownTimer.cancel();
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }
    public void setTradeTimeLabel(Label label) { this.tradeTimeLabel = label; }
}
