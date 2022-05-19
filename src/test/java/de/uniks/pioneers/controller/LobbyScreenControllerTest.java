package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextMatchers;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyScreenControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    Provider<LobbyUserlistController> userlistControlerProvider;

    @Mock
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @Mock
    Provider<LoginScreenController> loginScreenControllerProvider;

    @InjectMocks
    LobbyUserlistController userlistController;

    @Mock
    LoginScreenController loginScreenController;

    @Mock
    EventListener eventListener;

    @Mock
    UserService userService;

    @Mock
    LobbyService lobbyService;

    @Mock
    MessageService messageService;

    @Mock
    PrefService prefService;

    @InjectMocks
    LobbyScreenController lobbyScreenController;

    @Override
    public void start(Stage stage) {
        when(userlistControlerProvider.get()).thenReturn(userlistController);

        when(userService.editProfile(null,null,null,"online")).thenReturn(Observable.just(new User("","","","")));
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(new User("","","","")));
        when(userService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(userService.getCurrentUser()).thenReturn(new User("","","",null));

        when(eventListener.listen("users.*.*", User.class)).thenReturn(Observable.just(new Event<>("", new User("","","",""))));
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(Observable.just(new Event<>("", new Game("","","","","",0))));

        when(lobbyService.getGames()).thenReturn(Observable.just(new ArrayList<>()));
        when(lobbyService.logout()).thenReturn(Observable.just(new LogoutResult()));

        app.start(stage);
        app.show(lobbyScreenController);
    }

    @Test
    void logout() {
        User testUser = new User("id","n","online","a");
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(testUser));

        write("\t");
        type(KeyCode.ENTER);

        verify(userService).editProfile(null,null,null,"offline");
    }
}