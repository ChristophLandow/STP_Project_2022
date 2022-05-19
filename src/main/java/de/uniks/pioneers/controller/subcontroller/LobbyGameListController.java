package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


@Singleton
public class LobbyGameListController {

    private final UserlistService userlistService;
    private final Provider<GameListElementController> gameListElementControllerProvider;

    private final EventListener eventListener;
    private final LobbyService lobbyService;
    public ListView<Node> listViewGames;

    private final ObservableList<Game> games = FXCollections.observableArrayList();

    public ObservableList<Game> getGames() {
        return games;
    }

    private ObservableList<User> users = FXCollections.observableArrayList();
    private final List<GameListElementController> gameListElementControllers = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public LobbyGameListController(EventListener eventListener,
                                   LobbyService lobbyService,
                                   UserlistService userlistService,
                                   Provider<GameListElementController> gameListElementControllerProvider
                                    ) {
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userlistService = userlistService;
        this.gameListElementControllerProvider = gameListElementControllerProvider;
    }

    public void init(){
        // after leaving a game this methods get called again, thats why the item list gets cleared
        listViewGames.getItems().clear();

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(game -> System.out.println("game added +"+game.name()));
                c.getAddedSubList().stream().filter(this::isGameValid)
                                            .forEach(this::renderGame);
            } else if (c.wasRemoved()) {
                // for some reason this does not work anymore
                c.getRemoved().forEach(this::deleteGame);
            } else if (c.wasUpdated()) {
                c.getList().forEach(this::updateGame);
            }
        });

        disposable.add(lobbyService.getGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(this.games::setAll,
                        Throwable::printStackTrace));

        eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    if (gameEvent.event().endsWith(".created")) {
                        games.add(gameEvent.data());
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        games.remove(gameEvent.data());
                    } else {
                        //updateGame(gameEvent.data());
                    }
                });


        this.users=userlistService.getUsers();
    }


    private void renderGame(Game game) {
        System.out.println("rendering "+ game._id());
        GameListElementController gameListElementController = gameListElementControllerProvider.get();
        Parent node = gameListElementController.render();
        node.setId(game._id());
        User creator = returnUserById(game.owner());
        //System.out.println(creator.name());
        gameListElementController.creator.set(creator);
        gameListElementController.game.set(game);
        gameListElementController.setDataToGameListElement();
        gameListElementControllers.add(gameListElementController);
        listViewGames.getItems().add(0, node);
    }

    private boolean isGameValid(Game game) {
        // a game is valid as long his creator is online, we can update this if needed
        return users.stream().anyMatch(user -> user._id().equals(game.owner()));
    }

    public void deleteGame(Game data) {
        //find node belonging to game and then remove it from ListView
        try {
            List<Node> removales = (List<Node>) listViewGames.getItems().stream().toList();
            removales = removales.stream().filter(game -> game.getId().equals(data._id())).toList();
            listViewGames.getItems().removeAll(removales);
        } catch (Exception e) {
            System.err.println("Could not find game in game list");
        }
    }

    private void updateGame(Game game) {
        //rerender
        try {
            GameListElementController gameListElementController = gameListElementControllers.stream()
                    .filter(conroller -> conroller.game.get()._id().equals(game._id())).findAny().get();
            gameListElementController.game.set(game);
        } catch (Exception e) {
            System.err.println("Could not find controller in controller list");
        }
    }

    public User returnUserById(String id) {
        try {
            return users.stream().filter(user -> user._id().equals(id)).findAny().get();
        } catch (Exception e) {
            return null;
        }
    }

    public void stopListeners(){
        // someone has to find out how to stop the listeners, i tried to solve it in few minutes, takes more time
    }

}