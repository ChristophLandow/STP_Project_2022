package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LeaveGameController {
    private final NewGameScreenLobbyController newGameScreenLobbyController;
    private final NewGameLobbyService newGameLobbyService;
    private final UserService userService;
    private final PrefService prefService;
    private final GameService gameService;
    private List<User> users;
    private final List<Member> members;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private boolean leavedWithButton;
    private String myColor;
    @Inject Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    public LeaveGameController(NewGameScreenLobbyController newGameScreenLobbyController, NewGameLobbyService newGameLobbyService, UserService userService, PrefService prefService, GameService gameService) {
        this.newGameScreenLobbyController = newGameScreenLobbyController;
        this.newGameLobbyService = newGameLobbyService;
        this.userService = userService;
        this.prefService = prefService;
        this.gameService = gameService;
        this.users = new ArrayList<>();
        this.members = new ArrayList<>();
        this.leavedWithButton = false;
        this.myColor = "";
    }

    public void saveLeavedGame(String gameID, List<User> users, String myColor) {
        prefService.saveGameOnLeave(gameID);
        this.leavedWithButton = true;
        this.users = users;
        this.myColor = myColor;
    }

    public void loadLeavedGame(Game leavedGame) {
        if(leavedGame != null) {
            if(leavedWithButton) {
                toIngameScreen(leavedGame, myColor);
            } else {
                disposable.add(newGameLobbyService.getAll(leavedGame._id())
                        .observeOn(FX_SCHEDULER)
                        .subscribe(res -> {
                            this.members.addAll(res);
                            for (Member member : res) {
                                if (member.userId().equals(userService.getCurrentUser()._id())) {
                                    myColor = member.color();
                                }
                                users.add(userService.getUserById(member.userId()).blockingFirst());
                            }
                            toIngameScreen(leavedGame, myColor);
                        }, Throwable::printStackTrace));
            }
        }
    }

    private void toIngameScreen(Game leavedGame, String myColor) {
        gameService.loadPlayers(leavedGame);
        newGameScreenLobbyController.toIngame(leavedGame, users, myColor);
    }
}