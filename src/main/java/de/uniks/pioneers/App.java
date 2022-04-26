package de.uniks.pioneers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setTitle("Pioneers");

        final Scene scene = new Scene(new Label("Loading..."));
        stage.setScene(scene);

        primaryStage.show();
    }

}
