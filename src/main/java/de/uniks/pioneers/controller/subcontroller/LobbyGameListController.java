package de.uniks.pioneers.controller.subcontroller;

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
import java.util.stream.Collectors;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LobbyGameListController {
    private final UserlistService userlistService;
    private final EventListener eventListener;
    private final LobbyService lobbyService;
    public ListView<Node> listViewGames;
    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    private final Provider<GameListElementController> gameListElementControllerProvider;
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

    public void init() {
        this.users = userlistService.getUsers();

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderGame);
            }
        });

        Comparator<Game> gameComparator = new Comparator<>() {
            @Override
            public int compare(Game o1, Game o2) {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

        disposable.add(lobbyService.getGames()
                .observeOn(FX_SCHEDULER)
                .subscribe(games -> {
                            Collection<Game> validGames = games.stream().filter(game ->
                                    !game.started() && checkDate(game) && users.stream()
                                            .anyMatch(user -> user._id().equals(game.owner()))).toList();
                            this.games.setAll(validGames);
                            games.so
                            System.out.println(" amount of games " + games.size());
                        },
                        Throwable::printStackTrace));


        disposable.add(eventListener.listen("games.*.*", Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    if (gameEvent.event().endsWith(".created")) {
                        games.add(gameEvent.data());
                    } else if (gameEvent.event().endsWith(".deleted")) {
                        deleteGame(gameEvent.data());
                    } else {
                        updateGame(gameEvent.data());
                    }
                }));

    }

    private boolean checkDate(Game game) {
        // get date from somewhere
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String today = dtf.format(now);
        LocalDateTime nowMinusOneDay = LocalDateTime.now().minusDays(1);
        String yesterday = dtf.format(nowMinusOneDay);

        //get date from game
        String createdAt = game.createdAt();
        int end = createdAt.indexOf("T");
        String date = game.createdAt().substring(0, end);

        // is game from today or yesterday
        if (today.equals(date) || yesterday.equals(date)){
            return true;
        }else {
            return false;
        }
    }

    private void renderGame(Game game) {
        GameListElementController gameListElementController = gameListElementControllerProvider.get();
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
        GameListElementController gameListElementController = gameListElementControllers.stream()
                .filter(conroller -> conroller.game.get()._id().equals(game._id())).findAny().orElse(null);
        assert gameListElementController != null;
        gameListElementController.game.set(game);
    }

    public User returnUserById(String id) {
        return users.stream().filter(user -> user._id().equals(id)).findAny().orElse(null);
    }

    public void stop() {
        disposable.dispose();
    }
}
