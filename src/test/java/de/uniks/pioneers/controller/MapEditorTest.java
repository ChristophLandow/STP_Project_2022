package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.subcontroller.EditTile;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.services.MapService;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MapEditorTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    MapService mapService;

    @InjectMocks
    MapEditorController mapEditorController;

    @Override
    public void start(Stage stage){

        app.start(stage);
        app.show(mapEditorController);

    }

    @Test
    public void createMap(){


        //place tiles

        //clickOn(600, 150);

        clickOn("#iceImageView");
        System.out.println(mapEditorController.selection + "selection");
        clickOn("#0,-1,1");
        clickOn("#whaleImageView");
        clickOn("#1,-1,0");
        clickOn("#fishImageView");
        clickOn("#-1,0,1");
        clickOn("#randomImageView");
        clickOn("#0,0,0");
        clickOn("#desertImageView");
        clickOn("#1,0,-1");
        clickOn("#icebearImageView");
        clickOn("#-1,1,0");
        clickOn("#rockImageView");
        clickOn("#0,1,-1");

        //place numbers
        clickOn("#Circle2");
        clickOn("#0,-1,1");
        clickOn("#Circle3");
        clickOn("#1,-1,0");
        clickOn("#Circle4");
        clickOn("#-1,0,1");
        clickOn("#Circle5");
        clickOn("#0,0,0");
        clickOn("#Circle6");
        clickOn("#1,0,-1");
        clickOn("#Circle8");
        clickOn("#-1,1,0");
        clickOn("#Circle9");
        clickOn("#0,1,-1");

        //clickOn("#sizeSpinner");
        //mapEditorController.sizeSpinner.increment();
        //mapEditorController.sizeSpinner.setEditable(true);
        write("\t\t");
        press(KeyCode.UP);


        clickOn("#Circle10");
        clickOn("#2,-3,1");
        clickOn("#Circle11");
        clickOn("#3,-3,0");
        clickOn("#Circle12");
        clickOn("#3,-2,-1");

        //placing harbours
        clickOn("#harborFish");
        clickOn("#3,-3,0");
        clickOn("#harborIce");
        clickOn("#1,-1,0");
        clickOn("#1,-1,0");
        clickOn("#harborPolar");
        clickOn("#1,0,-1");
        clickOn("#harborGeneric");
        clickOn("#0,1,-1");
        clickOn("#0,1,-1");
        clickOn("#0,1,-1");
        clickOn("#harborWhale");
        clickOn("#-1,0,1");
        clickOn("#-1,0,1");
        clickOn("#harborCoal");
        clickOn("#0,-1,1");

        clickOn("#deleteButton");
        clickOn("#0,0,0");
        clickOn("#0,0,0");


        ArrayList<EditTile> verifier = new ArrayList<>();

        Polygon fillerView1 = new Polygon();
        javafx.scene.image.ImageView fillerView2 = new ImageView();
        verifier.add(new EditTile(new HexTile(-3, 0, 3, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-3, 1, 2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-3, 2, 1, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-3, 3, 0, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-2, -1, 3, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-2, 0, 2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-2, 1, 1, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-2, 2, 0, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-2, 3, -1, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-1, -2, 3, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-1, -1, 2, 1, false), fillerView1, fillerView2, null));

        HexTile hex1 = new HexTile(-1, 0, 1, 1, false);
        hex1.number = 4;
        hex1.type = "forest";
        EditTile edit1 = new EditTile(hex1, fillerView1, fillerView2, null);
        edit1.currentHarborSide = 9;
        edit1.currentHarborType = "harbour_grain";
        verifier.add(edit1);

        HexTile hex2 = new HexTile(-1, 1, 0, 0, false);
        hex2.number = 8;
        hex2.type = "pasture";
        EditTile edit2 = new EditTile(hex2, fillerView1, fillerView2, null);
        verifier.add(edit2);

        verifier.add(new EditTile(new HexTile(-1, 2, -1, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(-1, 3, -2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(0, -3, 3, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(0, -2, 2, 1, false), fillerView1, fillerView2, null));

        HexTile hex3 = new HexTile(0, -1, 1, 1, false);
        hex3.number = 2;
        hex3.type = "hills";
        EditTile edit3 = new EditTile(hex3, fillerView1, fillerView2, null);
        edit3.currentHarborSide = 1;
        edit3.currentHarborType = "harbour_ore";
        verifier.add(edit3);

        verifier.add(new EditTile(new HexTile(0, 0, 0, 1, false), fillerView1, fillerView2, null));

        HexTile hex4 = new HexTile(0, 1, -1, 1, false);
        hex4.number = 9;
        hex4.type = "mountains";
        EditTile edit4 = new EditTile(hex4, fillerView1, fillerView2, null);
        edit4.currentHarborSide = 7;
        edit4.currentHarborType = "harbour_general";
        verifier.add(edit4);

        verifier.add(new EditTile(new HexTile(0, 2, -2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(0, 3, -3, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(1, -3, 2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(1, -2, 1, 1, false), fillerView1, fillerView2, null));

        HexTile hex5 = new HexTile(1, -1, 0, 1, false);
        hex5.number = 3;
        hex5.type = "fields";
        EditTile edit5 = new EditTile(hex5, fillerView1, fillerView2, null);
        edit5.currentHarborSide = 3;
        edit5.currentHarborType = "harbour_brick";
        verifier.add(edit5);

        HexTile hex6 = new HexTile(1, 0, -1, 1, false);
        hex6.number = 6;
        hex6.type = "desert";
        EditTile edit6 = new EditTile(hex6, fillerView1, fillerView2, null);
        edit6.currentHarborSide = 3;
        edit6.currentHarborType = "harbour_wool";
        verifier.add(edit6);

        verifier.add(new EditTile(new HexTile(1, 1, -2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(1, 2, -3, 1, false), fillerView1, fillerView2, null));

        HexTile hex7 = new HexTile(2, -3, 1, 1, false);
        hex7.number = 10;
        EditTile edit7 = new EditTile(hex7, fillerView1, fillerView2, null);
        verifier.add(edit7);

        verifier.add(new EditTile(new HexTile(2, -2, 0, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(2, -1, -1, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(2, 0, -2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(2, 1, -3, 1, false), fillerView1, fillerView2, null));

        HexTile hex8 = new HexTile(3, -3, 0, 1, false);
        hex8.number = 11;
        EditTile edit8 = new EditTile(hex8, fillerView1, fillerView2, null);
        verifier.add(edit8);

        HexTile hex9 = new HexTile(3, -2, -1, 1, false);
        hex9.number = 12;
        EditTile edit9 = new EditTile(hex9, fillerView1, fillerView2, null);
        verifier.add(edit9);

        verifier.add(new EditTile(new HexTile(3, -1, -2, 1, false), fillerView1, fillerView2, null));
        verifier.add(new EditTile(new HexTile(3, 0, -3, 1, false), fillerView1, fillerView2, null));

        assertTrue(compareTiles(verifier, mapEditorController.tiles));

    }

    private Boolean compareTiles(ArrayList<EditTile> a, ArrayList<EditTile> b){

        if(a.size() != b.size()){return false;}
        int i = 0;

        Boolean equal = true;
        while(i < a.size()){

            EditTile tileA = a.get(i);
            EditTile tileB = b.get(i);
            //System.out.println(tileA);
            //System.out.println(tileB);
            //System.out.println();
            if(tileA.hexTile.q != tileB.hexTile.q){equal = false;}
            if(tileA.hexTile.r != tileB.hexTile.r){equal = false;}
            if(tileA.hexTile.s != tileB.hexTile.s){equal = false;}
            if(tileA.hexTile.number != tileB.hexTile.number){equal = false;}
            if(!tileA.hexTile.type.equals(tileB.hexTile.type)){equal = false;}
            if(tileA.currentHarborSide != tileB.currentHarborSide){equal = false;}
            if(!tileA.currentHarborType.equals(tileB.currentHarborType)){equal = false;}
            i++;
        }
        return equal;

    }
}