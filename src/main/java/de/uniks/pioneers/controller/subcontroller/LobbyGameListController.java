package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.LobbyScreenController;
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
import java.util.*;
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
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final List<GameListElementController> gameListElementControllers = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public LobbyGameListController(EventListener eventListener,
                                   LobbyService lobbyService,
                                   UserlistService userlistService,
                                   Provider<GameListElementController> gameListElementControllerProvider,
                                   Provider<LobbyScreenController> lobbyScreenControllerProvider
    ) {
        this.eventListener = eventListener;
        this.lobbyService = lobbyService;
        this.userlistService = userlistService;
        this.gameListElementControllerProvider = gameListElementControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
    }

    public void init() {
        this.users = userlistService.getUsers();
        this.listViewGames = lobbyScreenControllerProvider.get().listViewGames;

        games.addListener((ListChangeListener<? super Game>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderGame);
            }
        });

        Comparator<Game> gameComparator = new Comparator<>() {
            @Override
            //2022-06-09T23:12:49.041Z"
            public int compare(Game o1, Game o2) {
                /*
                int start = o1.createdAt().indexOf("T");

                String dateO1 = o1.createdAt().substring(0, start);
                dateO1 = dateO1.replace("-","");
                Integer dateO1ToInt = Integer.parseInt(dateO1);

                String dateO2 = o2.createdAt().substring(0, start);
                dateO2 = dateO2.replace("-","");
                Integer date02ToInt = Integer.parseInt(dateO2);


                int end  = o1.createdAt().indexOf(".");
                String timeO1 = o1.createdAt().substring(start+1, end);
                Integer gameO1 = Integer.parseInt(timeO1);
                String time02 = o2.createdAt().substring(start+1, end);
                Integer game02 = Integer.parseInt(time02);*/

                return o1.createdAt().compareTo(o2.createdAt());
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
                            validGames = validGames.stream().sorted(gameComparator).toList();
                            this.games.setAll(validGames);
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
        List<Node> removales = (List<Node>) listViewGames.getItems().stream().toList();
        removales = removales.stream().filter(game -> game.getId().equals(data._id())).toList();
        listViewGames.getItems().removeAll(removales);
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
