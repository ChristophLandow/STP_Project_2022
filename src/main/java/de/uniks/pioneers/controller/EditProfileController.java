package de.uniks.pioneers.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class EditProfileController {
    @FXML public TextField newUsernameInput;
    @FXML public Button saveLeaveButton;
    @FXML public ImageView avatarImage;
    @FXML public Button uploadAvatarButton;
    @FXML public PasswordField oldPasswordInput;
    @FXML public PasswordField newPasswordInput;
    @FXML public PasswordField repeatNewPasswordInput;
    @FXML public ChoiceBox<Text> avatarChoiceBox;
    @FXML public Text usernameLabel;
}
