package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class MapDetailsSubcontroller {

    private ListView<HBox> mapListView;
    private Pane mapPreviewPane;
    private ImageView creatorImageView;
    private Text createdOutputText;
    private Text lastUpdatedOutputText;
    private Text sizeOutputText;
    private Text tilesOutputText;
    private Text creatorNameOutputText;

    public MapDetailsSubcontroller setMapListView(ListView<HBox> mapListView) {
        this.mapListView = mapListView;
        return this;
    }

    public MapDetailsSubcontroller setMapPreviewPane(Pane mapPreviewPane) {
        this.mapPreviewPane = mapPreviewPane;
        return this;
    }

    public MapDetailsSubcontroller setCreatorImageView(ImageView creatorImageView) {
        this.creatorImageView = creatorImageView;
        return this;
    }

    public MapDetailsSubcontroller setCreatedOutputText(Text createdOutputText) {
        this.createdOutputText = createdOutputText;
        return this;
    }

    public MapDetailsSubcontroller setLastUpdatedOutputText(Text lastUpdatedOutputText) {
        this.lastUpdatedOutputText = lastUpdatedOutputText;
        return this;
    }

    public MapDetailsSubcontroller setSizeOutputText(Text sizeOutputText) {
        this.sizeOutputText = sizeOutputText;
        return this;
    }

    public MapDetailsSubcontroller setTilesOutputText(Text tilesOutputText) {
        this.tilesOutputText = tilesOutputText;
        return this;
    }

    public MapDetailsSubcontroller setCreatorNameOutputText(Text creatorNameOutputText) {
        this.creatorNameOutputText = creatorNameOutputText;
        return this;
    }

    // TODO: update details when new map is clicked
    public void updateMapDetails() {
    }
}
