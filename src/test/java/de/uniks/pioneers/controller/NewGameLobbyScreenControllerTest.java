/*package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewGameLobbyScreenControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    PrefService prefService;

    @Mock
    NewGameLobbyService newGameLobbyService;

    @Mock
    EventListener eventListener;

    @Spy
    Provider<LobbyScreenController> lobbyScreenControllerProvider;

    @Spy
    Provider<RulesScreenController> rulesScreenControllerProvider;

    @Spy
    Provider<LobbyGameListController> lobbyGameListControllerProvide;

    @Spy
    App app = new App(null);

    @Mock(name = "gameChatControllerProvider")
    Provider<GameChatController> gameChatControllerProvider;

    @InjectMocks
    GameChatController gameChatController;

    @InjectMocks
    NewGameScreenLobbyController newGameScreenLobbyController;


    private Game testGame = new Game("1", "2", "3", "name", "owner", 2);
    private Member member01;
    private Member member02;
    private Member nowMember = new Member("3", "3", "3", "3", true);

    private MessageDto message01 = new MessageDto("1","1","1","member01","hello there");
    private MessageDto messaga02 = new MessageDto("2","2","2","member02","how are you ");

    private String patternToObserveGameMembers;
    private User owner  = new User("1", "owner", "online", "avatar");
    private User user02  = new User("2","member02","online","");
    private User userJoining = new User("3", "testor01", "online", "avatar");

    @Override
    public void start(Stage stage) {

        app = new App(null);
        when(prefService.recall()).thenReturn("");
        when(gameChatControllerProvider.get()).thenReturn(gameChatController);
        patternToObserveGameMembers = String.format("games.%s.members.*.*", testGame._id());
        when(eventListener.listen(patternToObserveGameMembers, Member.class)).
                thenReturn(Observable.just(new Event<Member>("games.3.member.3.created", nowMember)));
        when(userService.getUserById("1")).thenReturn(Observable.just(owner));
        when(userService.getUserById("2")).thenReturn(Observable.just(user02));
        when(userService.getUserById("3")).thenReturn(Observable.just(userJoining));

        //MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        newGameScreenLobbyController.game.set(testGame);
        app.start(stage);
        app.show(newGameScreenLobbyController);
    }

    @Test
    void postNewMemberNotOwner() {

        newGameScreenLobbyController.owner.set(owner);
        newGameScreenLobbyController.setPassword("12345678");

        member01 = new Member("1", "2", "3", "1", true);
        member02 = new Member("2", "2", "3", "2", true);

        when(eventListener.listen(patternToObserveGameMembers, Member.class)).
                thenReturn(Observable.just(new Event<Member>("games.3.member.3.created", nowMember)));

        when(gameChatControllerProvider.get()).thenReturn(gameChatController);
        when(newGameLobbyService.getMessages(testGame._id()))
                .thenReturn(Observable.just(List.of(message01,messaga02)));

        when(newGameLobbyService.getMessages(testGame._id())).thenReturn(Observable.just(List.of(message01,messaga02)));
        when(userService.getUserById("1")).thenReturn(Observable.just(owner));
        when(userService.getUserById("2")).thenReturn(Observable.just(user02));
        when(userService.getUserById("3")).thenReturn(Observable.just(userJoining));
        when(eventListener.listen("games." + testGame._id() + ".messages.*.*", MessageDto.class))
                .thenReturn(Observable.just(new Event<MessageDto>("games.3.messsages.1.created",message01)));
        when(newGameLobbyService.getAll("3")).thenReturn(Observable.just(List.of(member01, member02)));
        when(newGameLobbyService.postMember("3", true, "12345678"))
                .thenReturn(Observable.just(nowMember));

        newGameScreenLobbyController.postNewMember(userJoining, "12345678");

        List<Member> members = newGameScreenLobbyController.getMembers();

        assertEquals(members.get(1).createdAt(), "2");
        assertEquals(members.get(1).updatedAt(), "2");
        assertEquals(members.get(1).gameId(), "3");
        assertEquals(members.get(1).userId(), "2");
        assertTrue(members.get(1).ready());

        assertEquals(members.size(), 2);

        //check view
        //assertThat(newGameScreenLobbyController.userBox.getChildren().size()).isEqualTo(3);

        verify(userService).getUserById("1");
        verify(userService).getUserById("2");
        verify(userService).getUserById("3");
        verify(eventListener).listen(patternToObserveGameMembers,Member.class);
        //verify(gameChatControllerProvider).get();
        verify(newGameLobbyService).getMessages(testGame._id());
        verify(eventListener.listen("games." + testGame._id() + ".messages.*.*", MessageDto.class));
        verify(newGameLobbyService).getAll("3");
        verify(newGameLobbyService).postMember("3", true, "12345678");

    }
}


 */

