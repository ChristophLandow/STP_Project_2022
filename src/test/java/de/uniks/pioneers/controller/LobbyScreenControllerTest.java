package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyScreenControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    LobbyUserlistController userlistController;

    @InjectMocks
    LobbyGameListController lobbyGameListController;

    @InjectMocks
    LoginScreenController loginScreenController;

    @Mock(name = "userlistControlerProvider")
    Provider<LobbyUserlistController> userlistControlerProvider;

    @Mock(name = "lobbyGameListControllerProvider")
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @Mock(name = "loginScreenControllerProvider")
    Provider<LoginScreenController> loginScreenControllerProvider;

    @Mock
    EventListener eventListener;

    @Mock
    UserService userService;

    @Mock
    LobbyService lobbyService;

    @Mock
    UserlistService userlistService;

    @Mock
    MessageService messageService;

    @Mock
    PrefService prefService;


    @InjectMocks
    LobbyScreenController lobbyScreenController;

    @Override
    public void start(Stage stage) {
        //Setup lobby controller API calls
        when(prefService.recall()).thenReturn("");

        when(userService.editProfile(null,null,null,"online")).thenReturn(Observable.just(new User("","","","")));
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(new User("","","","")));
        when(userService.getCurrentUser()).thenReturn(new User("","","",null));

        when(eventListener.listen("games.*.*", Game.class)).thenReturn(Observable.just(new Event<>("", new Game("","","","","",0))));

        when(lobbyService.getGames()).thenReturn(Observable.just(new ArrayList<>()));
        when(userlistService.getUsers()).thenReturn(FXCollections.observableArrayList());
        when(lobbyService.logout()).thenReturn(Observable.just(new LogoutResult()));
        when(messageService.getchatUserList()).thenReturn(FXCollections.observableArrayList());

        when(userlistControlerProvider.get()).thenReturn(userlistController);
        when(lobbyGameListControllerProvider.get()).thenReturn(lobbyGameListController);
        when(loginScreenControllerProvider.get()).thenReturn(loginScreenController);

        //Start controller
        app.start(stage);
        app.show(lobbyScreenController);
    }

    @Test
    void logout() {
        User testUser = new User("id","n","online","a");
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(testUser));

        //Hit logout button
        write("\t");
        type(KeyCode.ENTER);

        //Check if logout functions were called
        verify(userService).editProfile(null,null,null,"offline");
        verify(lobbyService).logout();
    }
}