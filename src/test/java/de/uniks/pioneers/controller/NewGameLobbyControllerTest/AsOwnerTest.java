package de.uniks.pioneers.controller.NewGameLobbyControllerTest;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.controller.subcontroller.ColorPickerController;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.controller.subcontroller.NewGameLobbyReadyController;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AsOwnerTest extends ApplicationTest {
    @Mock
    UserService userService;

    @Mock
    NewGameLobbyService newGameLobbyService;

    @Mock
    PrefService prefService;

    @Mock
    EventListener eventListener;

    @Mock
    ColorPickerController colorPickerController;

    @Mock
    NewGameLobbyReadyController newGameLobbyReadyController;

    @Spy
    App app = new App(null);

    @Mock(name = "gameChatController")
    GameChatController gameChatController;

    @InjectMocks
    NewGameScreenLobbyController newGameScreenLobbyController;

    final String randomColor02 = createRandomColor();
    final String randomColor03 = createRandomColor();
    final String randomAvatar01 = createRandomAvatar();
    final String randomAvatar02 = createRandomAvatar();
    final String randomAvatar03 = createRandomAvatar();

    private String patternToObserveGameMembers;
    private String patternToObserveUserOwner;
    private String patternToObserveUserUser02;
    private String patternToObserveUserJoining;

    private final Game testGame = new Game("1", "2", "3", "name", "1", 2,false, null);
    private final Member member01 = new Member("1", "2", "3", "1", true, "#0075ff",false);
    private final Member member02 = new Member("2", "2", "3", "2", true, randomColor02,false);
    private final Member nowMember = new Member("3", "3", "3", "3", true, randomColor03,false);
    private final MessageDto message01 = new MessageDto("1","1","1","1","hello there");
    private final MessageDto message02 = new MessageDto("2","2","2","2","how are you ");
    private final User owner  = new User("1", "owner", "online", randomAvatar01);
    private final User user02  = new User("2","member02","online",randomAvatar02);
    private final User userJoining = new User("3", "userJoining", "online", randomAvatar03);

    AsOwnerTest() throws IOException, URISyntaxException {}

    @Override
    public void start(Stage stage) {
        patternToObserveGameMembers = String.format("games.%s.members.*.*", testGame._id());
        patternToObserveUserOwner = String.format("users.%s.updated", owner._id());
        patternToObserveUserUser02 = String.format("users.%s.updated", user02._id());
        patternToObserveUserJoining = String.format("users.%s.updated", userJoining._id());
        String patternToObserveGame = String.format("games.%s.*", testGame._id());

        when(app.getStage()).thenReturn(stage);

        when(userService.getCurrentUser()).thenReturn(owner);

        when(eventListener.listen(patternToObserveUserOwner, User.class)).thenReturn(Observable.just(new Event<>("users.1.updated", owner)));
        when(eventListener.listen(patternToObserveUserUser02, User.class)).thenReturn(Observable.just(new Event<>("users.2.updated", user02)));
        when(eventListener.listen(patternToObserveUserJoining, User.class)).thenReturn(Observable.just(new Event<>("users.3.updated", userJoining)));

        when(newGameLobbyService.getAll("3")).thenReturn(Observable.just(List.of(member01, member02)));

        when(userService.getUserById("1")).thenReturn(Observable.just(owner));
        when(userService.getUserById("2")).thenReturn(Observable.just(user02));
        when(userService.getUserById("3")).thenReturn(Observable.just(userJoining)).thenReturn(Observable.just(userJoining));

        when(eventListener.listen(patternToObserveGameMembers, Member.class)).thenReturn(Observable.just(new Event<>("games.3.member.3.created", nowMember)));
        when(eventListener.listen(patternToObserveGame, Game.class)).thenReturn(Observable.just(new Event<>("games.3.updated", testGame)));
        when(newGameLobbyService.getMembers()).thenReturn(FXCollections.observableArrayList());

        newGameScreenLobbyController.setPassword("12345678");
        newGameScreenLobbyController.setGame(testGame);
        app.start(stage);
        app.show(newGameScreenLobbyController);
    }

    @Test
    void initControllerAsOwner() {
        List<Member> members = newGameLobbyService.getMembers();

        assertEquals(members.get(1).createdAt(), "2");
        assertEquals(members.get(1).updatedAt(), "2");
        assertEquals(members.get(1).gameId(), "3");
        assertEquals(members.get(1).userId(), "2");
        assertTrue(members.get(1).ready());

        assertEquals(members.size(), 2);

        // assertions for current user box, colorpicker gets random color
        FxAssert.verifyThat("#gameNameLabel", LabeledMatchers.hasText("name"));
        FxAssert.verifyThat("#passwordLabel", LabeledMatchers.hasText("12345678"));
        FxAssert.verifyThat("#clientUserNameLabel", LabeledMatchers.hasText("owner"));

        //assertion for member box, have to add some assertions
        assertThat(newGameScreenLobbyController.userBox.getChildren().size()).isEqualTo(2);

        //assert create game button is disabled
        Button startGameButton = lookup("#startGameButton").query();
        assertThat(startGameButton.disableProperty().get()).isEqualTo(false);

        // post member to game, server broadcasting event to clients -> eventlistenter.listen(game -> memberCounter+1)
        newGameScreenLobbyController.memberCount.set(3);
        assertThat(startGameButton.disableProperty().get()).isEqualTo(false);

        verify(newGameLobbyService).getAll("3");
        //verify(newGameLobbyService).logout();
        verify(userService,times(1)).getUserById("1");
        verify(userService,times(1)).getUserById("2");
        verify(userService).getUserById("3");
        verify(eventListener).listen(patternToObserveUserOwner, User.class);
        verify(eventListener).listen(patternToObserveUserUser02, User.class);
        verify(eventListener).listen(patternToObserveUserJoining, User.class);
        verify(eventListener).listen(patternToObserveGameMembers, Member.class);
        //verify(newGameLobbyService).getMessages(testGame._id());
        //verify(eventListener).listen("games." + testGame._id() + ".messages.*.*", MessageDto.class);

        Platform.runLater(() -> assertThat(newGameLobbyReadyController.onSetReadyButton(new ActionEvent())).isEqualTo(false));
        Platform.runLater(() -> assertThat(newGameLobbyReadyController.allUsersReady()).isEqualTo(false));
    }

    public String createRandomColor()
    {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

    public String createRandomAvatar() throws IOException, URISyntaxException {

        List<String> avatarNames = Arrays.asList("elephant.png", "giraffe.png", "hippo.png", "monkey.png",
                "panda.png", "parrot.png", "penguin.png", "pig.png", "rabbit.png", "snake.png");
        Random rand = new Random();
        String avatarStr = avatarNames.get(rand.nextInt(avatarNames.size()));
        String newAvatar;

        if(Objects.requireNonNull(getClass().getResource("images/" + avatarStr)).toString().contains("!")) {
            final Map<String, String> env = new HashMap<>();
            String[] array = Objects.requireNonNull(getClass().getResource("images/" + avatarStr)).toString().split("!");
            FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
            byte[] data = Files.readAllBytes(Objects.requireNonNull(fs.getPath(array[1])));
            newAvatar = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
            fs.close();
        }
        else
        {
            byte[] data = Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("images/" + avatarStr)).toURI()));
            newAvatar = "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
        }

        return newAvatar;
    }
}



