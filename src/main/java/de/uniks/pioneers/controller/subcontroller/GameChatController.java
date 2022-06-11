package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.inject.Inject;

import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameChatController {
    private ScrollPane chatScrollPane;
    private VBox messageBox;
    private TextField messageText;
    private Button sendButton;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private final UserService userService;
    private final NewGameLobbyService newGameLobbyService;

    private Game game;
    private List<User> users;

    @Inject
    public GameChatController(NewGameLobbyService newGameLobbyService, EventListener eventListener, UserService userService){
        this.newGameLobbyService = newGameLobbyService;
        this.eventListener = eventListener;
        this.userService = userService;
    }

    public void render(){
        messageBox.heightProperty().addListener(u->chatScrollPane.setVvalue(1));

        if (sendButton != null) {
            sendButton.setOnAction(this::sendMessage);
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

        System.out.println("games." + game._id() + ".messages.*.*");
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

        Image userImage;
        try {
            userImage = new Image(user.avatar());
        } catch (IllegalArgumentException | NullPointerException e) {
            userImage = new Image(Constants.DEFAULT_AVATAR);
        }

        ImageView avatarView = new ImageView(userImage);
        avatarView.setFitHeight(25);
        avatarView.setFitWidth(25);
        Label textLabel = new Label(user.name() + ": " + message.body());
        textLabel.setFont(new Font(15));
        HBox hbox = new HBox(avatarView, textLabel);
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

    private void sendMessage(ActionEvent actionEvent) {
        String message = this.messageText.getText();
        if(!message.isEmpty()) {
            disposable.add(newGameLobbyService.sendMessage(game._id(), new CreateMessageDto(message))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> this.messageText.clear()));
        }
    }

    public GameChatController setChatScrollPane(ScrollPane chatScrollPane) {
        this.chatScrollPane = chatScrollPane;
        return this;
    }

    public GameChatController setMessageBox(VBox messageBox) {
        this.messageBox = messageBox;
        return this;
    }

    public GameChatController setMessageText(TextField messageText) {
        this.messageText = messageText;
        return this;
    }

    public GameChatController setSendButton(Button sendButton) {
        this.sendButton = sendButton;
        return this;
    }

    public GameChatController setGame(Game game) {
        this.game = game;
        return this;
    }

    public GameChatController setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
