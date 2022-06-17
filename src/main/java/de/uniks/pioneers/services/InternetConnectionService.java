package de.uniks.pioneers.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Timer;
import java.util.TimerTask;

public class InternetConnectionService {
    private boolean connected;
    private final Alert alert;
    private String command;

    public InternetConnectionService() {
        this.connected = true;
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.getButtonTypes().clear();
        this.alert.getDialogPane().setMinHeight(100);
        this.checkConnection();

        if(System.getProperty("os.name").startsWith("Windows")) {
            command = "ping -n 1 www.google.com";
        } else {
            command = "ping -c 1 www.google.com";
        }
    }

    public void checkConnection() {
        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Process p1 = java.lang.Runtime.getRuntime().exec(command);
                    int returnVal1 = p1.waitFor();
                    p1.destroy();

                    Process p2 = java.lang.Runtime.getRuntime().exec(command);
                    int returnVal2 = p2.waitFor();
                    p2.destroy();

                    if(returnVal1 == 0 || returnVal2 == 0) {
                        if(!connected) {
                            connected = true;
                            Platform.runLater(this::restoredConnection);
                        }
                    } else {
                        if(connected) {
                            connected = false;
                            Platform.runLater(this::lostConnection);
                            Platform.runLater(this::showAlert);
                        }
                    }
                } catch(Exception e) {
                    if(connected) {
                        connected = false;
                        Platform.runLater(this::lostConnection);
                        Platform.runLater(this::showAlert);
                    }
                }
            }

            private void lostConnection() {
                alert.getButtonTypes().clear();
                alert.setHeaderText("Lost Internet connection");
            }

            private void restoredConnection() {
                alert.getButtonTypes().add(ButtonType.OK);
                alert.setHeaderText("Internet connection restored");
            }

            private void showAlert() {
                alert.showAndWait();
            }
        };

        timer.schedule(myTask, 0, 10000);
    }
}