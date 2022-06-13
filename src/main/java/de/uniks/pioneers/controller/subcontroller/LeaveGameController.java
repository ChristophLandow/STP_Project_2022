package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.controller.RulesScreenController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LeaveGameController {
    private final NewGameScreenLobbyController newGameScreenLobbyController;
    private final NewGameLobbyService newGameLobbyService;
    private final App app;
    private final UserService userService;
    private final PrefService prefService;
    private final List<User> users;
    private final List<Member> members;
    private final CompositeDisposable disposable = new CompositeDisposable();
    @Inject Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    public LeaveGameController(App app, NewGameScreenLobbyController newGameScreenLobbyController, NewGameLobbyService newGameLobbyService, UserService userService, PrefService prefService) {
        this.app = app;
        this.newGameScreenLobbyController = newGameScreenLobbyController;
        this.newGameLobbyService = newGameLobbyService;
        this.userService = userService;
        this.prefService = prefService;
        this.users = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void saveLeavedGame(String gameID) {
        prefService.saveGameOnLeave(gameID);
    }

    public void loadLeavedGame() {
        Game leavedGame = prefService.getSavedGame();

        disposable.add(newGameLobbyService.getAll(leavedGame._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(res -> {
                    String myColor = "";
                    this.members.addAll(res);
                    for (Member member : res) {
                        if(member.userId().equals(userService.getCurrentUser()._id())) {
                            myColor = member.color();
                        }
                        users.add(userService.getUserById(member.userId()).blockingFirst());
                    }
                    toIngameScreen(leavedGame, myColor);
                }, Throwable::printStackTrace));
    }

    private void toIngameScreen(Game leavedGame, String myColor) {
        newGameScreenLobbyController.toIngame(leavedGame, users, myColor);
    }
}
