package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AvatarSpinnerController extends SpinnerValueFactory<Integer> {

    private Map<Integer, Pair<Image,String>> images = new LinkedHashMap<>();
    protected ImageView avatarImageView;
    public Consumer<String> changeAvatar;

    public AvatarSpinnerController(Consumer<String> changeAvatar) {
        this.changeAvatar=changeAvatar;
    }

    public void init(ImageView avatarImageView) {

        String elephantURL = "images/elephant.png";
        Image image = new Image(getClass().getResource(elephantURL).toString());
        images.put(1,new Pair<>(image,elephantURL));

        String giraffeURL = "images/giraffe.png";
        image = new Image(getClass().getResource(giraffeURL).toString());
        images.put(2,new Pair<>(image,giraffeURL));

        String hippoURL = "images/hippo.png";
        image = new Image(getClass().getResource(hippoURL).toString());
        images.put(3,new Pair<>(image,hippoURL));

        String monkeyURL = "images/monkey.png";
        image = new Image(getClass().getResource(monkeyURL).toString());
        images.put(4,new Pair<>(image,monkeyURL));

        String pandaURL = "images/panda.png";
        image = new Image(getClass().getResource(pandaURL).toString());
        images.put(5,new Pair<>(image,pandaURL));

        String parrotURL = "images/parrot.png";
        image = new Image(getClass().getResource(parrotURL).toString());
        images.put(6,new Pair<>(image,parrotURL));

        String penguinURL = "images/penguin.png";
        image = new Image(getClass().getResource(penguinURL).toString());
        images.put(7,new Pair<>(image,penguinURL));

        String pigURL = "images/pig.png";
        image = new Image(getClass().getResource(pigURL).toString());
        images.put(8,new Pair<>(image,pigURL));

        String rabbitURL = "images/rabbit.png";
        image = new Image(getClass().getResource(rabbitURL).toString());
        images.put(9,new Pair<>(image,rabbitURL));

        String snakeURL = "images/snake.png";
        image = new Image(getClass().getResource(snakeURL).toString());
        images.put(10,new Pair<>(image,snakeURL));

        this.avatarImageView = avatarImageView;

        this.initImageView();
    }

    protected void initImageView() {
        setValue(1);
        Pair <Image,String> pair = images.get(1);
        avatarImageView.setImage(pair.getKey());
        changeAvatar.accept(pair.getValue());
    }
    @Override
    public void decrement(int steps) {
        if (getValue() <= 1) {
            setValue(10);
        } else {
            int oldValue = getValue();
            setValue(oldValue - 1);
        }
        Pair <Image,String> pair = images.get(getValue());
        avatarImageView.setImage(pair.getKey());
        changeAvatar.accept(pair.getValue());
    }
    @Override
    public void increment(int steps) {
        if (getValue() == 10) {
            setValue(1);
        } else {
            int oldValue = getValue();
            setValue(oldValue + 1);
        }
        Pair <Image,String> pair = images.get(getValue());
        avatarImageView.setImage(pair.getKey());
        changeAvatar.accept(pair.getValue());
    }
}
