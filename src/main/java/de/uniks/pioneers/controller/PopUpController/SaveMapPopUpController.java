package de.uniks.pioneers.controller.PopUpController;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.io.IOException;

public class SaveMapPopUpController implements Controller {

    public TextField mapNameTextField;
    public TextArea descriptionTextArea;
    public Button cancelButton;
    public Button saveButtonPopUp;

    @Inject
    public SaveMapPopUpController() {

    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/PopUps/SaveMapPopUp.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    @Override
    public void stop() {

    }

}
