package de.uniks.pioneers;

import de.uniks.pioneers.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

import static de.uniks.pioneers.Constants.LOGIN_SCREEN_TITLE;

public class App extends Application {
    private Stage stage;
    private Controller controller;

    public App() {
        MainComponent mainComponent = DaggerMainComponent.builder().mainApp(this).build();
        this.controller = mainComponent.loginController();
    }

    public App(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        if(this.controller != null) {
            stage.setTitle(LOGIN_SCREEN_TITLE);
            setIcons(stage);
            show(this.controller);
        }
        stage.show();

        //new InternetConnectionService();
    }

    @Override
    public void stop() {
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

    public void setIcons(Stage stage) {
        String iconName = "AppIcon.png";
        Image icon = new Image(Objects.requireNonNull(App.class.getResource(iconName)).toString());
        stage.getIcons().add(icon);
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                if (Taskbar.isTaskbarSupported()) {
                    final Taskbar taskbar = Taskbar.getTaskbar();
                    final java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource(iconName)));
                    taskbar.setIconImage(image);
                }
            } catch (Exception ignored) {}
        }
    }
}


