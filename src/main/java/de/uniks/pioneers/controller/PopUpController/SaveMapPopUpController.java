package de.uniks.pioneers.controller.PopUpController;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.MapBrowserController;
import de.uniks.pioneers.controller.MapEditorController;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.services.MapService;
import de.uniks.pioneers.services.StylesService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveMapPopUpController implements Controller {

    @FXML
    public TextField mapNameTextField;
    @FXML
    public TextArea descriptionTextArea;
    @FXML
    public Button cancelButton;
    @FXML
    public Button saveButtonPopUp;

    @FXML
    public VBox root;

    private final Provider<MapBrowserController> mapBrowserControllerProvider;
    private MapEditorController mapEditorController;

    private final MapService mapService;

    private final StylesService stylesService;

    private List<EditTile> editTiles = new ArrayList<>();
    private final App app;


    @Inject
    public SaveMapPopUpController(Provider<MapBrowserController> mapBrowserControllerProvider, MapService mapService, StylesService stylesService, App app) {
        this.mapBrowserControllerProvider = mapBrowserControllerProvider;
        this.mapService = mapService;
        this.stylesService = stylesService;
        this.app = app;
    }

    @Override
    public void init() {
        String globalStyles = "/de/uniks/pioneers/styles/globalStyles.css";
        String globalStylesDark = "/de/uniks/pioneers/styles/globalStylesDark.css";
        stylesService.setStyleSheets(app.getStage().getScene().getStylesheets(), globalStyles, globalStylesDark);
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
        root.toBack();
        this.cancelButton.setOnAction(e -> cancel());
        this.saveButtonPopUp.setOnAction(e -> save());

        return view;
    }

    @Override
    public void stop() {

    }

    public void showSavePopUp() {
        root.setDisable(false);
        root.setVisible(true);
        root.toFront();
        disableEditor(true);
    }

    public void hideSavePopUp() {
        root.setDisable(true);
        root.setVisible(false);
        root.toBack();
        disableEditor(false);
    }

    public void cancel() {
        this.hideSavePopUp();
    }

    public void save() {
        String name = mapNameTextField.getText();
        String description = descriptionTextArea.getText();
        mapService.updateOrCreateMap(this.editTiles, name, description);
        this.hideSavePopUp();
        MapBrowserController mapBrowserController = mapBrowserControllerProvider.get();
        this.app.show(mapBrowserController);
    }

    public void setTiles(List<EditTile> editTiles) {
        this.editTiles = editTiles;
    }

    public void setMapEditorController(MapEditorController mapEditorController) {
        this.mapEditorController = mapEditorController;
    }

    public void disableEditor(Boolean disable) {
        //disable or able everything accept the popUp
        for (Node n : mapEditorController.mapEditorAnchorPane.getChildren()) {
            if (!n.equals(root)) {
                n.setDisable(disable);
            }
        }
    }
}
