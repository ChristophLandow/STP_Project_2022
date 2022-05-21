package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.DaggerTestComponent;
import de.uniks.pioneers.MainComponent;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewGameLobbyScreenControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    NewGameLobbyService newGameLobbyService;

    @Mock
    EventListener eventListener;

    @Mock
    PrefService prefService;

    @Spy
    Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Spy
    Provider<RulesScreenController> rulesScreenControllerProvider;

    @Spy
    Provider<LobbyGameListController> lobbyGameListControllerProvide;

    @Spy
    App app = new App(null);

    @InjectMocks
    NewGameScreenLobbyController newGameScreenLobbyController;

    //private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private Game testGame = new Game("1", "2", "3", "name2", "owner2", 2);
    private final List<User> users = new ArrayList<>();
    private final ObservableList<Member> members = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        newGameScreenLobbyController.game.set(testGame);
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }

    @Test
    void postNewMemberNotOwner() {
        User owner = new User("1", "owner", "online", "avatar");

        newGameScreenLobbyController.owner.set(owner);
        newGameScreenLobbyController.setPassword("12345678");

        Member member02 = new Member("2", "2", "3", "2", true);
        Member member01 = new Member("1", "2", "3", "1", true);

        newGameScreenLobbyController.getMembers().add(member01);
        newGameScreenLobbyController.getMembers().add(member02);

        when(newGameLobbyService.postMember("3", true, "12345678"))
                .thenReturn(Observable.just(new Member("3", "3", "3", "3", true)));

        //check data model before method call
        List<Member> members = newGameScreenLobbyController.getMembers();

        assertEquals(members.size(), 2);
        assertEquals(members.get(1).createdAt(), "2");
        assertEquals(members.get(1).updatedAt(), "2");
        assertEquals(members.get(1).gameId(), "3");
        assertEquals(members.get(1).userId(), "2");
        assertTrue(members.get(1).ready());

        User userJoining = new User("2", "testor01", "online", "avatar");
        newGameScreenLobbyController.postNewMember(testGame, userJoining, "12345678");

        //check data model afterwards
        assertEquals(members.size(), 3);

        //check view
        assertThat(newGameScreenLobbyController.userBox.getChildren().size()).isEqualTo(3);

        verify(newGameLobbyService).postMember("3", true, "12345678");

            /*
            das kannst du mit when(eventListener.listen("games.1.updatet").thenReturn(new Event(blablabla)) machen
           [12:51]
           und eventListener entsprechend mocken */

    }
}


