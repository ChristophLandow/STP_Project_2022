package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameStorage;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class IngamePlayerListController {

    public ListView<IngamePlayerListElementController> listViewGames;
    private final List<IngamePlayerListElementController> elementControllers = new ArrayList<>();

    @Inject
    GameStorage gameStorage;

    @Inject
    Provider <IngamePlayerListElementController> ingamePlayerListElementControllerProvider;

    @Inject
    public IngamePlayerListController() {}



    public void init(ListView<IngamePlayerListElementController> playerListView) {

        this.listViewGames = playerListView;
        gameStorage.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            c.wasAdded();
            if (c.wasAdded()) {
                renderPlayer(c.getValueAdded());
            }else if (c.wasRemoved()){
                removePlayer(c.getValueRemoved());
            }else{
                updatePlayer(c.getValueAdded());
            }
        });

    }


    private void renderPlayer(Player valueAdded) {
    }

    private void updatePlayer(Player valueAdded) {
    }

    private void removePlayer(Player valueRemoved) {
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
        try {
            GameListElementController gameListElementController = gameListElementControllers.stream()
                    .filter(conroller -> conroller.game.get()._id().equals(game._id())).findAny().get();
            gameListElementController.game.set(game);
        } catch (Exception e) {
            System.err.println("Could not find controller in controller list");
        }
    }

}
