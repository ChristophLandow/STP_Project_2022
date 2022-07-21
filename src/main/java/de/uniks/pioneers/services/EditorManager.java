package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.HexTile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.GameConstants.eulerC;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class EditorManager {

    @Inject
    public EditorManager(){}

    public List<HexTile> buildFrame(int size, int scale) {

        List<HexTile> frame = new ArrayList<>();

        for(int q = -size; q <= size; q++){
            for(int r = max(-size, -q-size); r <= min(+size, -q+size); r++){

                int s = -q-r;
                frame.add(new HexTile(q,r,s, scale, true));

            }
        }
        return frame;
    }
}
