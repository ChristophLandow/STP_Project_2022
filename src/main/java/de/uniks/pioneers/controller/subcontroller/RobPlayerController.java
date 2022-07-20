package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.RobberService;
import de.uniks.pioneers.services.StylesService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class RobPlayerController implements Controller {
    @FXML public MenuButton menuButton;
    @FXML public HBox selectedItemBox;
    @FXML public ListView<HBox> playerListView;
    @FXML public Button acceptButton;
    public AnchorPane robAnchorpane;
    private Stage stage;
    @Inject PrefService prefService;
    @Inject RobberService robberService;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final StylesService stylesService;

    @Inject
    public RobPlayerController(StylesService stylesService) {
        this.stylesService = stylesService;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/RobPlayerPopUp.fxml"));
        loader.setControllerFactory(c->this);
        final Parent robView;
        try {
            robView = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(User u : robberService.getRobbingCandidates()){
            ImageView img = new ImageView(new Image(u.avatar(), 20, 20, true, true));
            Label label = new Label(u.name());

            HBox box = new HBox(img, label);
            box.setOnMouseClicked(this::setNewSelectedPlayer);
            this.playerListView.getItems().add(box);
        }

        ImageView img = new ImageView(new Image(robberService.getRobbingCandidates().get(0).avatar(), 50, 50, true, true));
        Label label = new Label(robberService.getRobbingCandidates().get(0).name());
        playerListView.getSelectionModel().select(0);

        selectedItemBox.getChildren().clear();
        selectedItemBox.getChildren().addAll(img,label);

        playerListViewSize();

        return robView;
    }

    @Override
    public void init() {
        this.stage = new Stage();
        Parent node = render();
        this.stage.setTitle("Discard resource cards");
        Scene scene = new Scene(node);
        this.stage.setScene(scene);
        stylesService.setStyleSheets(scene.getStylesheets());

        this.stage.show();

        this.menuButton.setOnMouseClicked(this::playerListToggleVisibility);
        this.robAnchorpane.setOnMouseClicked(this::setPlayerListInvisible);
        this.acceptButton.setOnMouseClicked(this::okClicked);

        openListViewWithSpace(this.menuButton);
        choosePlayerWithSpace(this.playerListView);
        pressOkWithSpace(acceptButton);
    }

    @Override
    public void stop() {
        robberService.getRobberState().set(GameConstants.ROBBER_FINISHED);
        stage.close();
        disposable.dispose();
    }

    private void setNewSelectedPlayer(MouseEvent event){
        User u = robberService.getRobbingCandidates().get(this.playerListView.getSelectionModel().getSelectedIndex());

        ImageView img = new ImageView(new Image(u.avatar(), 50, 50, true, true));
        Label label = new Label(u.name());

        this.selectedItemBox.getChildren().clear();
        this.selectedItemBox.getChildren().addAll(img,label);
    }

    private void playerListToggleVisibility(MouseEvent event){
        this.playerListView.setVisible(!this.playerListView.isVisible());
    }

    private void setPlayerListInvisible(MouseEvent event){
        this.playerListView.setVisible(false);
    }

    private void playerListViewSize(){
        if(playerListView.getItems().size() < 5){
            playerListView.setPrefHeight(playerListView.getItems().size() * 27);
        }
    }

    private void openListViewWithSpace(Node node) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                this.playerListView.setVisible(!this.playerListView.isVisible());
                event.consume();
            }
        });
    }

    private void choosePlayerWithSpace(Node node) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                this.playerListView.getSelectionModel().getSelectedItem().fireEvent(
                        new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                                MouseButton.PRIMARY, 1, false, false, false,
                                false, false, false, false,
                                false, true, false, null));
                event.consume();
            }
        });
    }

    private void pressOkWithSpace(Node node){
        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                this.acceptButton.fireEvent(
                        new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                                MouseButton.PRIMARY, 1, false, false, false,
                                false, false, false, false,
                                false, true, false, null));
                event.consume();
            }
        });
    }

    public void okClicked(MouseEvent event){
        User userChosen = robberService.getRobbingCandidates().get(this.playerListView.getSelectionModel().getSelectedIndex());
        disposable.add(this.robberService.robPlayer(userChosen._id()).observeOn(FX_SCHEDULER).subscribe(move -> stop(), this::handleHttpError));
    }

    private  void handleHttpError(Throwable exception) throws IOException {
        String errorBody;
        if (exception instanceof HttpException httpException) {
            errorBody = Objects.requireNonNull(Objects.requireNonNull(httpException.response()).errorBody()).string();
        } else {
            return;
        }

        System.out.println("!!!An Http Error appeared!!!\n" + errorBody);
    }
}
