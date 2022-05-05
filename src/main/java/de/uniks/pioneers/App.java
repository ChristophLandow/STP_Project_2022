package de.uniks.pioneers;

import de.uniks.pioneers.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static de.uniks.pioneers.Constants.LOGIN_SCREEN_TITLE;


public class App extends Application {
    private Stage stage;
    private Controller controller;

    public App(){

        MainComponent mainComponent = DaggerMainComponent.builder().mainApp(this).build();
        this.controller = mainComponent.loginController();
    }
    public App(Controller controller){

        this.controller = controller;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        stage.setTitle(LOGIN_SCREEN_TITLE);
        show(this.controller);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        cleanup();
    }

    public void cleanup() {
        if (controller != null) {
            controller.stop();
            controller = null;
        }
    }

    public void show(Controller controller) {
        cleanup();
        this.controller = controller;
        stage.setScene(new Scene(controller.render()));
        controller.init();
    }

    public Stage getStage() {
        return stage;
    }
}


