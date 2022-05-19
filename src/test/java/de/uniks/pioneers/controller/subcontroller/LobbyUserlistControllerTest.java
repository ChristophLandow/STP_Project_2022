package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.services.UserlistService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyUserlistControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @Spy
    Provider<LobbyUserlistController> lobbyUserlistControllerProvider;

    @Spy
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @InjectMocks
    LobbyScreenController lobbyScreenController;

    @InjectMocks
    LobbyUserlistController userlistController;

    @InjectMocks
    LobbyGameListController lobbyGameListController;

    @Mock
    UserService userService;

    @Mock
    LobbyService lobbyService;

    @Mock
    UserlistService userlistService;

    @Mock
    EventListener eventListener;

    @Override
    public void start(Stage stage){
        when(userService.getCurrentUser()).thenReturn(new User("","","",null));
        when(userService.editProfile(null,null,null,"online")).thenReturn(Observable.just(new User("","","","")));
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(new User("","","","")));
        when(userService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(userService.getCurrentUser()).thenReturn(new User("","","",null));

        when(userlistService.getUsers()).thenReturn(FXCollections.observableArrayList());

        when(eventListener.listen("users.*.*", User.class)).thenReturn(Observable.just(new Event<>("", new User("","","",""))));
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(Observable.just(new Event<>("", new Game("","","","","",0))));

        when(lobbyService.getGames()).thenReturn(Observable.just(new ArrayList<>()));
        when(lobbyService.logout()).thenReturn(Observable.just(new LogoutResult()));

        when(lobbyGameListControllerProvider.get()).thenReturn(lobbyGameListController);
        when(lobbyUserlistControllerProvider.get()).thenReturn(userlistController);

        app.start(stage);
        app.show(lobbyScreenController);
    }

    @Test
    void renderUser() {
    }

    @Test
    void removeUser() {
    }

    @Test
    void updateUser() {
    }
}