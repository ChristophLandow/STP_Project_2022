package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


@Singleton
public class MapDetailsController {

    private Pane mapPreviewPane; // need it later for the map preview
    private ImageView creatorImageView;
    private Text lastUpdatedOutputText;
    private Text votesOutputText;
    private Text tilesOutputText;
    private Text harborsOutputText;
    private Text mapNameOutputText;
    private Text createdByOutputText;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MapBrowserService mapBrowserService;
    private final UserService userService;

    @Inject
    public MapDetailsController(MapBrowserService mapBrowserService, UserService userService) {
        this.mapBrowserService = mapBrowserService;
        this.userService = userService;
    }

    public MapDetailsController setMapPreviewPane(Pane mapPreviewPane) {
        this.mapPreviewPane = mapPreviewPane;
        return this;
    }

    public void setCreatorImageView(ImageView creatorImageView) {
        this.creatorImageView = creatorImageView;
    }

    // update details when new map is clicked
    public void updateMapDetails(String mapId) {
        disposable.add(mapBrowserService.getMap(mapId)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::setLabels));
    }

    private void setLabels(MapTemplate mapTemplate) {
        lastUpdatedOutputText.setText(toDateTimeString(mapTemplate.updatedAt()));
        votesOutputText.setText(String.valueOf(mapTemplate.votes()));
        tilesOutputText.setText(String.valueOf(mapTemplate.tiles().size()));
        harborsOutputText.setText(String.valueOf(mapTemplate.harbors().size()));
        mapNameOutputText.setText(mapTemplate.name());

        disposable.add(userService.getUserById(mapTemplate.createdBy())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    createdByOutputText.setText(user.name());
                    if (user.avatar() != null) {
                        creatorImageView.setImage(new Image(user.avatar()));
                    }
                })
        );
    }

    private String toDateTimeString(String timeString) {
        String date = timeString.substring(0, 10);
        String time = timeString.substring(11, 16);
        return date + ", " + time;
    }

    public MapDetailsController setLastUpdatedOutputText(Text lastUpdatedOutputText) {
        this.lastUpdatedOutputText = lastUpdatedOutputText;
        return this;
    }

    public MapDetailsController setVotesOutputText(Text votesOutputText) {
        this.votesOutputText = votesOutputText;
        return this;
    }

    public MapDetailsController setTilesOutputText(Text tilesOutputText) {
        this.tilesOutputText = tilesOutputText;
        return this;
    }

    public MapDetailsController setHarborsOutputText(Text harborsOutputText) {
        this.harborsOutputText = harborsOutputText;
        return this;
    }

    public MapDetailsController setMapNameOutputText(Text mapNameOutputText) {
        this.mapNameOutputText = mapNameOutputText;
        return this;
    }

    public MapDetailsController setCreatedByOutputText(Text createdByOutputText) {
        this.createdByOutputText = createdByOutputText;
        return this;
    }
}
