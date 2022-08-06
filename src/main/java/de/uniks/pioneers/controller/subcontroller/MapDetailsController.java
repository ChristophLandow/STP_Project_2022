package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.BoardController;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.MapRenderService;
import de.uniks.pioneers.services.MapService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


@Singleton
public class MapDetailsController {

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

    private final MapService mapService;
    private ScrollPane previewScrollPane;
    private AnchorPane previewAnchorPane;
    private Pane previewPane;
    private Canvas previewCanvas;

    @Inject Provider<BoardController> boardControllerProvider;
    @Inject Provider<MapRenderService> mapRenderServiceProvider;
    @Inject Provider<ZoomableScrollPane> zoomableScrollPaneProvider;
    private BoardController boardController;
    private MapRenderService mapRenderService;
    private ZoomableScrollPane zoomPaneController;

    @Inject
    public MapDetailsController(MapBrowserService mapBrowserService, UserService userService, MapService mapService) {
        this.mapBrowserService = mapBrowserService;
        this.userService = userService;
        this.mapService = mapService;
    }

    public void init() {
        this.mapRenderService = mapRenderServiceProvider.get();
        this.zoomPaneController = zoomableScrollPaneProvider.get();
        this.boardController = boardControllerProvider.get();

        mapRenderService.setFinishedLoading(false);
        boardController.fieldPane = this.previewPane;
        //zoomPaneController.init(true, previewScrollPane, previewAnchorPane, previewPane, previewCanvas);

        Platform.runLater(zoomPaneController::render);
    }

    public void setPreviewElements(ScrollPane scrollPane, AnchorPane anchorPane, Pane pane, Canvas canvas) {
        this.previewScrollPane = scrollPane;
        this.previewAnchorPane = anchorPane;
        this.previewPane = pane;
        this.previewCanvas = canvas;
    }

    public MapDetailsController setCreatorImageView(ImageView creatorImageView) {
        this.creatorImageView = creatorImageView;
        return this;
    }

    // update details when new map is clicked
    public void updateMapDetails(String mapId) {
        disposable.add(mapBrowserService.getMap(mapId)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::setLabels));
    }

    private void setLabels(MapTemplate mapTemplate) {
        //set current map
        mapService.setCurrentMap(mapTemplate);
        //set Labels
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

        showPreview(mapTemplate);
    }

    private void showPreview(MapTemplate mapTemplate) {
        // clear recent map preview
        this.previewPane.getChildren().clear();
        this.previewCanvas.getGraphicsContext2D().clearRect(0,0, previewCanvas.getWidth(), previewCanvas.getHeight());
        previewPane.getChildren().add(this.previewCanvas);

        // init zoomPane
        this.zoomPaneController.init(true, previewScrollPane, previewAnchorPane, previewPane, previewCanvas);
        this.boardController.buildMapPreview(mapTemplate, previewPane);
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
