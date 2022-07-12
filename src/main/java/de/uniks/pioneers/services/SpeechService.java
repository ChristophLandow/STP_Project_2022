package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class SpeechService {
    @Inject
    public SpeechService() {
    }

    public void play(String file){
        //playAudio(GameConstants.FEMALE, file);
    }

    private void playAudio(String gender, String file){
        String filepath = Objects.requireNonNull(getClass().getResource("speech/" + gender + "/" + file + ".mp3")).toString();
        Media hit = new Media(filepath);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }
}
