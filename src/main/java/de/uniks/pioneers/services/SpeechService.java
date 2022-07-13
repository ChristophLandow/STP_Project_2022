package de.uniks.pioneers.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class SpeechService {
    final PrefService prefService;
    @Inject
    public SpeechService(PrefService prefService) {
        this.prefService = prefService;
    }

    public void play(String file){
        if(prefService.getVoiceOutputActive()){
            playAudio(prefService.getGenderVoice(), file);
        }
    }

    private void playAudio(String gender, String file){
        String filepath = Objects.requireNonNull(getClass().getResource("speech/" + gender + "/" + file + ".mp3")).toString();
        Media hit = new Media(filepath);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }
}
