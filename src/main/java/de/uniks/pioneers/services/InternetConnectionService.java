package de.uniks.pioneers.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class InternetConnectionService {
    private boolean connected;
    private Alert alert;

    public InternetConnectionService() {
        this.connected = true;
        this.alert = new Alert(Alert.AlertType.ERROR);
        this.alert.getButtonTypes().clear();
        this.alert.getDialogPane().setMinHeight(100);
        this.checkConnection();
    }

    public void checkConnection() {
        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    int timeout = 2000;
                    InetAddress[] addresses = InetAddress.getAllByName("www.google.com");
                    for(InetAddress address : addresses) {
                        if(address.isReachable(timeout)) {
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

        timer.schedule(myTask, 2000, 2000);
    }
}