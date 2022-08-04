package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.controller.IngameScreenController;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.PrefService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameChatController {
    @Inject PrefService prefService;
    private ScrollPane chatScrollPane;
    private VBox messageBox;
    private TextField messageText;
    private Button sendButton;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private final UserService userService;
    private final NewGameLobbyService newGameLobbyService;
    private IngameScreenController ingameScreenController;
    private Game game;
    private List<User> users;

    @Inject
    public GameChatController(NewGameLobbyService newGameLobbyService, EventListener eventListener, UserService userService) {
        this.newGameLobbyService = newGameLobbyService;
        this.eventListener = eventListener;
        this.userService = userService;
    }

    public void render(){
        messageBox.heightProperty().addListener(u->chatScrollPane.setVvalue(1));

        if (sendButton != null) {
            sendButton.setOnAction(actionEvent -> sendMessage());
        } else {
            messageText.setOnKeyPressed(this::sendMessageViaEnter);
        }

        this.messages.addListener((ListChangeListener<? super MessageDto>) c->{
            c.next();
            if(c.wasAdded()){
                c.getAddedSubList().forEach(this::renderMessage);
            }
        });
    }

    private void sendMessageViaEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String message = this.messageText.getText();
            if(!message.isEmpty()) {
                disposable.add(newGameLobbyService.sendMessage(game._id(), new CreateMessageDto(message))
                        .observeOn(FX_SCHEDULER)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe(result -> this.messageText.clear()));
            }
        }
    }

    public void init(){
        disposable.add(newGameLobbyService.getMessages(game._id()).observeOn(FX_SCHEDULER)
                .subscribe(this.messages::setAll));

        disposable.add(eventListener.listen("games." + game._id() + ".messages.*.*", MessageDto.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(messageEvent -> {
                    final MessageDto message = messageEvent.data();
                    if (messageEvent.event().endsWith(".created") && !messageAlreadyRendered(message)){
                        messages.add(message);
                    }
                }));
    }

    public void stop(){
        disposable.dispose();
    }

    public void renderMessage(MessageDto message){
        User user = null;
        for(User u: users){
            if(u._id().equals(message.sender())){
                user = u;
            }
        }

        if(user == null){
            user = userService.getUserById(message.sender()).blockingFirst();
        }

        if(user._id().equals(game.owner()) && message.body().equals("Host left the Game!")) {
            if(!userService.getCurrentUser()._id().equals(game.owner())) {
                ingameScreenController.leave();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
                alert.showAndWait();
            }
            return;
        }

        Image userImage;
        try {
            userImage = new Image(user.avatar());
        } catch (IllegalArgumentException | NullPointerException e) {
            userImage = new Image(Constants.DEFAULT_AVATAR);
        }

        ImageView avatarView = new ImageView(userImage);
        avatarView.setFitHeight(25);
        avatarView.setFitWidth(25);

        Text messageText = new Text(user.name() + ": " + message.body());
        messageText.setFont(new Font(16));
        TextFlow textFlow = new TextFlow(messageText);

        if(prefService.getDarkModeState()){
            textFlow.setId("textMessage");
        }
        HBox hbox = new HBox(avatarView, textFlow);
        hbox.setSpacing(7);

        this.messageBox.getChildren().add(hbox);
    }

    public boolean messageAlreadyRendered(MessageDto message){
        for(MessageDto m : messages){
            if(m._id().equals(message._id())){
                return true;
            }
        }
        return false;
    }

    private void sendMessage() {
        String message = this.messageText.getText();
        if(!message.isEmpty()) {
            disposable.add(newGameLobbyService.sendMessage(game._id(), new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> this.messageText.clear()));
        }
    }

    public void sendMessage(String message, Game game) {
        if(!message.isEmpty()) {
            disposable.add(newGameLobbyService.sendMessage(game._id(), new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe());
        }
    }

    public void setChatScrollPane(ScrollPane chatScrollPane) {
        this.chatScrollPane = chatScrollPane;
    }

    public void setMessageBox(VBox messageBox) {
        this.messageBox = messageBox;
    }

    public void setMessageText(TextField messageText) {
        this.messageText = messageText;
    }

    public void setSendButton(Button sendButton) {
        this.sendButton = sendButton;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setIngameScreenController (IngameScreenController ingameScreenController) {
        this.ingameScreenController = ingameScreenController;
    }
}
