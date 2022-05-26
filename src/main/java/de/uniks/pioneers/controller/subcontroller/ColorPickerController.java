package de.uniks.pioneers.controller.subcontroller;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public class ColorPickerController {
    private ColorPicker colorPicker;
    private SVGPath houseSVG;
    private String hexColor;

    public ColorPickerController(ColorPicker colorPicker, SVGPath houseSVG)
    {
        this.colorPicker = colorPicker;
        this.houseSVG = houseSVG;
        this.houseSVG.setFill(Paint.valueOf(colorPicker.getValue().toString()));
    }

    public void setColor() {
        this.houseSVG.setFill(Paint.valueOf(colorPicker.getValue().toString()));
    }

    public void setColor(String hexColor) {
        this.hexColor = hexColor;
        this.colorPicker.setValue(Color.valueOf(hexColor));
        this.setColor();
    }

    public String getColor() {
        return hexColor;
    }

    public void setDisable(boolean disable)
    {
        this.colorPicker.setDisable(disable);
    }
}
