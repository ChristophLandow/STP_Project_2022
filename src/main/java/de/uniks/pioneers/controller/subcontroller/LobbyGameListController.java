package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class LobbyGameListController {
    private final UserlistService userlistService;
    private final EventListener eventListener;
    private final LobbyService lobbyService;
    private final App app;
    public ListView<Node> listViewGames;
    private ObservableList<Game> games;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private final Provider<GameListElementController> gameListElementControllerProvider;
    private final List<GameListElementController> gameListElementControllers = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private boolean darkMode = false;

    @Inject
    public LobbyGameListController(App app, EventListener eventListener,
                                   LobbyService lobbyService,
                                   UserlistService userlistService,
                                   Provider<GameListElementController> gameListElementControllerProvider) {
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userlistService = userlistService;
        this.gameListElementControllerProvider = gameListElementControllerProvider;
        this.app = app;
    }

    public void setup() {
        this.users = userlistService.getUsers();
        listViewGames.getItems().clear();
        games = FXCollections.observableArrayList();
        disposable = new CompositeDisposable();

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderGame);
            }
        });

        disposable.add(lobbyService.getGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(games -> {
                            /*
                             add game to list when game was not started, the date is today
                             or yesterdays date and the owner is online
                             */
                            Collection<Game> validGames = games.stream().filter(game -> !game.started() && checkDate(game)
                            && users.stream().anyMatch(user -> user._id().equals(game.owner()))).toList();
                            validGames = validGames.stream().sorted(gameComparator).toList();
                            this.games.setAll(validGames);
                        },
                        Throwable::printStackTrace));


        disposable.add(eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    if (gameEvent.event().endsWith(".created") && !games.contains(gameEvent.data())) {
                        games.add(gameEvent.data());
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        deleteGame(gameEvent.data());
                    } else {
                        updateGame(gameEvent.data());
                    }
                }));

    }

    private boolean checkDate(Game game) {
        // get date from server
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String today = dtf.format(now);
        LocalDateTime nowMinusOneDay = LocalDateTime.now().minusDays(1);
        String yesterday = dtf.format(nowMinusOneDay);
        //get date from game
        String createdAt = game.createdAt();
        int end = createdAt.indexOf("T");
        String date = game.createdAt().substring(0, end);

        // if game was created yesterday or today return true
        return today.equals(date) || yesterday.equals(date);
    }

    Comparator<Game> gameComparator = new Comparator<>() {
        @Override
        public int compare(Game o1, Game o2) {
            return o1.createdAt().compareTo(o2.createdAt());
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    private void renderGame(Game game) {
        GameListElementController gameListElementController = gameListElementControllerProvider.get();
        if(darkMode){
            gameListElementController.getApp().getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_LobbyGameList.css");
        } else {
            gameListElementController.getApp().getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/LobbyGameList.css");
        }
        Parent node = gameListElementController.render();
        node.setId(game._id());
        User creator = returnUserById(game.owner());
        gameListElementController.creator.set(creator);
        gameListElementController.game.set(game);
        gameListElementController.setDataToGameListElement();
        gameListElementControllers.add(gameListElementController);
        listViewGames.getItems().add(0, node);
    }


    public void deleteGame(Game data) {
        //find node belonging to game and then remove it from ListView
        List<Node> removales = (List<Node>) listViewGames.getItems().stream().toList();
        removales = removales.stream().filter(game -> game.getId().equals(data._id())).toList();
        listViewGames.getItems().removeAll(removales);
    }

    private void updateGame(Game game) {
        //rerender
        GameListElementController gameListElementController = gameListElementControllers.stream()
                .filter(conroller -> conroller.game.get()._id().equals(game._id())).findAny().orElse(null);

        try {
            gameListElementController.game.set(game);
        } catch (NullPointerException ignored) {

        }
    }

    public User returnUserById(String id) {
        return users.stream().filter(user -> user._id().equals(id)).findAny().orElse(null);
    }

    public void stop() {
        disposable.dispose();
    }

    public void setDarkMode() {
        darkMode = true;
    }

    public void setBrightMode() {
        darkMode = false;
    }

    public App getApp() {
        return this.app;
    }
}
