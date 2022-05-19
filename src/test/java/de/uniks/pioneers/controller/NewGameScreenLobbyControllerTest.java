package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.services.NewGameLobbyService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class NewGameScreenLobbyControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    NewGameLobbyService newGameLobbyService;

    @InjectMocks
    NewGameScreenLobbyController newGameScreenLobbyController;

    @Override
    public void start(Stage stage) {
        app.start(stage);
        app.show(newGameScreenLobbyController);
    }

    @Test
    public void setReadyTrue() {
        when(newGameLobbyService.setReady(anyString(), anyString())).thenReturn(Observable.just(new Member("now", "now", "1", "2", true)));
    }

    @Test
    public void showReadyCheckMark() {

    }
}