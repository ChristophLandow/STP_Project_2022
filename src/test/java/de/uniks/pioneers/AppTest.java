package de.uniks.pioneers;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage){

        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());

    }

    @Test
    public void test() {


        //Test module still provisionally uses Server functionality! Implementation of tests may require adjustment to respective classes in TestModule class!


    }

}