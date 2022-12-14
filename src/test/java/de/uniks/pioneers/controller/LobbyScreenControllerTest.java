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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyScreenControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    LobbyUserlistController userlistController;

    @InjectMocks
    LobbyGameListController lobbyGameListController;

    @Mock(name = "userlistControllerProvider")
    Provider<LobbyUserlistController> userlistControllerProvider;

    @Mock(name = "lobbyGameListControllerProvider")
    Provider<LobbyGameListController> lobbyGameListControllerProvider;

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

    @Mock
    StylesService stylesService;

    @InjectMocks
    LobbyScreenController lobbyScreenController;

    @Override
    public void start(Stage stage) {

        // get date from server
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String today = dtf.format(now);

        Game testGame = new Game(today+"T18:12:58.114Z","2022-05-18T18:12:58.114Z","001","TestGameA","001",1,false, null);

        when(userService.editProfile(null,null,null,"online")).thenReturn(Observable.just(new User("","","","")));
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(new User("","","","")));
        when(userService.getCurrentUser()).thenReturn(new User("","","",null));

        when(lobbyService.getGames()).thenReturn(Observable.just(List.of(testGame)));
        when(eventListener.listen("games.*.*", Game.class)).thenReturn(Observable.just(new Event<>("games.001.updated",testGame)));

        when(userlistService.getUsers()).thenReturn(FXCollections.observableArrayList());
        when(lobbyService.logout()).thenReturn(Observable.just(new LogoutResult()));

        when(userlistControllerProvider.get()).thenReturn(userlistController);
        when(lobbyGameListControllerProvider.get()).thenReturn(lobbyGameListController);

        //Start controller
        app.start(stage);
        app.show(lobbyScreenController);
        verify(stylesService).setStyleSheets(any(), anyString(), anyString());
        verify(prefService, atLeastOnce()).getSavedGame();
    }

    @Test
    void logout() {
        User testUser = new User("id","n","online","a");
        when(userService.editProfile(null,null,null,"offline")).thenReturn(Observable.just(testUser));

        //Hit logout button
        write("\t");
        type(KeyCode.SPACE);

        //Check if logout functions were called
        verify(userService).editProfile(null,null,null,"offline");
        verify(lobbyService).logout();
    }
}