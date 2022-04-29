package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

;

public class AvatarSpinnerController extends SpinnerValueFactory<Integer> {

    private Map<Integer, Pair<Image,String>> images = new LinkedHashMap<>();
    private ImageView avatarImageView;
    public Consumer<String> changeAvatar;

    public AvatarSpinnerController(Consumer<String> changeAvatar) {
        this.changeAvatar=changeAvatar;
    }

    public void init(ImageView avatarImageView) {

        Image image = new Image(getClass().getResource("images/flegmon.png").toString());
        String flegmonURL = "images/flegmon.png";
        images.put(1,new Pair<>(image,flegmonURL));

        image = new Image(getClass().getResource("images/mew.png").toString());
        String mewURL = "images/mew.png";
        images.put(2,new Pair<>(image,mewURL));

        image = new Image(getClass().getResource("images/enton.png").toString());
        String entonURL = "images/enton.png";
        images.put(3,new Pair<>(image,entonURL));

        image = new Image(getClass().getResource("images/gyrados.png").toString());
        String gyradosURL = "images/gyrados.png";
        images.put(4,new Pair<>(image,gyradosURL));

        image = new Image(getClass().getResource("images/arkani.png").toString());
        String arkaniURL = "images/arkani.png";
        images.put(5,new Pair<>(image,arkaniURL));

        image = new Image(getClass().getResource("images/shiggy.png").toString());
        String shiggyURL = "images/shiggy.png";
        images.put(6,new Pair<>(image,shiggyURL));

        image = new Image(getClass().getResource("images/pinzor.png").toString());
        String pinzorURL = "images/pinzor.png";
        images.put(7,new Pair<>(image,pinzorURL));

        image = new Image(getClass().getResource("images/glurak.png").toString());
        String glurakURL = "images/glurak.png";
        images.put(8,new Pair<>(image,glurakURL));

        image = new Image(getClass().getResource("images/bisaflor.png").toString());
        String bisarflorURL = "images/bisaflor.png";
        images.put(9,new Pair<>(image,bisarflorURL));

        image = new Image(getClass().getResource("images/bisasam.png").toString());
        String bisasamURL = "images/bisasam.png";
        images.put(10,new Pair<>(image,bisasamURL));

        this.avatarImageView = avatarImageView;

        setValue(1);
        Pair <Image,String> pair = images.get(1);
        avatarImageView.setImage(pair.getKey());
        changeAvatar.accept(pair.getValue());
        ;
    }

    @Override
    public void decrement(int steps) {
        if (getValue() == 1) {
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
