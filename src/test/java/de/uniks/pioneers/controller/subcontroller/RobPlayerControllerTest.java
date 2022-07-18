package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.RobberService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RobPlayerControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);
    @InjectMocks RobPlayerController robPlayerController;
    @Mock RobberService robberService;
    @Mock PrefService prefService;

    MouseEvent leftClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
            MouseButton.PRIMARY, 1, false, false, false,
            false, false, false, false,
            false, true, false, null);

    @Override
    public void start(Stage stage){
        ArrayList<User> robbingCandidates = new ArrayList<>();
        robbingCandidates.add(new User("player1","player1","online", Constants.DEFAULT_AVATAR));
        robbingCandidates.add(new User("player2","player2","online", Constants.DEFAULT_AVATAR));

        RobDto robMove = new RobDto(0,0,0,"player1");

        when(prefService.getDarkModeState()).thenReturn(false);
        when(robberService.getRobbingCandidates()).thenReturn(robbingCandidates);
        when(robberService.robPlayer("player1")).thenReturn(Observable.just(new Move("", "","1", "u","rob",0, null, robMove, null, "")));

        app.start(stage);
        app.show(robPlayerController);
    }

    @Test
    void test() {

        Pane anchorPane = lookup("#robAnchorpane").query();
        anchorPane.fireEvent(leftClick);

        MenuButton menuButton = lookup("#menuButton").query();
        menuButton.fireEvent(leftClick);

        sleep(2000);
        ListView<HBox> playerListView = lookup("#playerListView").query();
        //assertTrue(playerListView.isVisible());

        playerListView.fireEvent(leftClick);
        type(KeyCode.DOWN);
        type(KeyCode.SPACE);
        menuButton.fireEvent(leftClick);

        sleep(2000);
        assertFalse(playerListView.isVisible());

        sleep(2000);

        HBox selectedItemBox = lookup("#selectedItemBox").query();
        Label playerLabel = (Label) selectedItemBox.getChildren().get(1);
        assertEquals(playerLabel.getText(), "player1");

        sleep(1500);

        write("\t");
        type(KeyCode.SPACE);

        verify(robberService, atLeastOnce()).robPlayer("player1");
    }
}