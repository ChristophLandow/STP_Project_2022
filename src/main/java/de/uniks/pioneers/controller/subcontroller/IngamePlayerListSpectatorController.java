package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class IngamePlayerListSpectatorController {
    @FXML public Pane playerBox;
    @FXML public Label playerName;
    @FXML public ImageView playerAvatar;
    @FXML private Button kickButton;

    private ListView<Node> nodeListView;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final UserService userService;
    private final EventListener eventListener;
    private final NewGameLobbyService newGameLobbyService;
    private boolean online;
    private String gameID;
    private String playerID;

    @Inject
    public IngamePlayerListSpectatorController(UserService userService, EventListener eventListener, NewGameLobbyService newGameLobbyService) {
        this.userService = userService;
        this.eventListener = eventListener;
        this.newGameLobbyService = newGameLobbyService;
    }

    public void init(String gameID, String playerID) {
        this.gameID = gameID;
        this.playerID = playerID;
    }

    public void render(String ownerID) {
        Parent node;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/IngamePlayerListSpectator.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
            nodeListView.getItems().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.playerBox.setId(playerID);

        // set values to gui and setup listeners
        disposable.add(userService.getUserById(playerID)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    if(!user.avatar().equals("")) {
                        playerAvatar.setImage(new Image(user.avatar()));
                    }
                    playerName.setText(user.name());
                    addUserListener(user._id());
                })
        );

        if(!ownerID.equals(userService.getCurrentUser()._id())) {
            kickButton.setVisible(false);
        }
    }

    private void addUserListener(String id) {
        String patternToObserveUser = String.format("users.%s.updated", id);
        disposable.add(eventListener.listen(patternToObserveUser, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    User userFromEvent = userEvent.data();
                    Background background = new Background(new BackgroundFill(Color.WHITE, null, null));
                    if (userFromEvent.status().equals("offline")) {
                        background = new Background(new BackgroundFill(Color.TOMATO, null, null));
                        playerBox.setBackground(background);
                        playerBox.setOpacity(40);
                        online = false;
                    } else if (userFromEvent.status().equals("online") && !online) {
                        playerBox.setBackground(background);
                        playerBox.setOpacity(100);
                        online = true;
                    }
                })
        );
    }

    public void setNodeListView(ListView<Node> nodeListView) {
        this.nodeListView = nodeListView;
    }

    public void onKickButtonClicked() {
        System.out.println(gameID + "   " + playerBox);
        disposable.add(newGameLobbyService.deleteMember(gameID, playerID)
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(response -> System.out.println("kick")));
    }

    public void stop() {
        disposable.dispose();
    }
}


