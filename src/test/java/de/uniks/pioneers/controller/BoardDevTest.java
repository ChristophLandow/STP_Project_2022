package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardDevTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    IngameScreenController controller;


    @Override
    public void start(Stage stage){

        app.start(stage);
        app.show(controller);
    }

    @Test
    public void test(){

        while(true){}

    }

}