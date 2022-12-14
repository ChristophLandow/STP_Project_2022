package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class SpeechService {
    boolean buildingPhaseOnce = true;

    @Inject PrefService prefService;
    @Inject
    public SpeechService() {

    }

    public void play(String file){
        if(prefService.getVoiceOutputActive() && (!file.equals(GameConstants.SPEECH_BUILD) || buildingPhaseOnce)){
            playAudio(prefService.getGenderVoice(), file);
        }

        if(file.equals(GameConstants.SPEECH_BUILD)){
            buildingPhaseOnce = false;
        }
    }

    private void playAudio(String gender, String file){
        String filepath = Objects.requireNonNull(getClass().getResource("speech/" + gender + "/" + file + ".mp3")).toString();
        Media hit = new Media(filepath);
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }
}
