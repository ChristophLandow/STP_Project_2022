package de.uniks.pioneers.controller.subcontroller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

import java.util.Iterator;

public class ColorPickerController {
    private ColorPicker colorPicker;
    private SVGPath houseSVG;
    private String hexColor;

    public ColorPickerController(ColorPicker colorPicker, SVGPath houseSVG)
    {
        this.colorPicker = colorPicker;
        this.houseSVG = houseSVG;
        this.houseSVG.setFill(Paint.valueOf(colorPicker.getValue().toString()));

        this.colorPicker.showingProperty().addListener((obs,b,b1) -> {
            if(b1) {
                PopupWindow popupWindow = getPopupWindow();
                Node popup = popupWindow.getScene().getRoot().getChildrenUnmodifiable().get(0);
                popup.lookupAll(".color-rect").stream().forEach(rect -> {
                    Color c = (Color)((Rectangle)rect).getFill();
                    // Replace with your custom color
                    ((Rectangle)rect).setFill(Color.RED);
                });
            }
        });
    }

    public void setColor() {
        checkColorDifference("#" + colorPicker.getValue().toString().substring(2,8));
        this.hexColor = "#" + colorPicker.getValue().toString().substring(2,8);
        this.houseSVG.setFill(Paint.valueOf(hexColor));
    }

    public void setColor(String hexColor) {
        this.hexColor = hexColor;
        this.colorPicker.setValue(Color.valueOf(hexColor));
        this.houseSVG.setFill(Paint.valueOf(hexColor));
    }

    public String getColor() {
        return hexColor;
    }

    public void setDisable(boolean disable)
    {
        this.colorPicker.setDisable(disable);
    }

    public boolean checkColorDifference(String otherColorString) {
        double[] ownLab = rgbToLab(this.hexColor);
        double[] otherLab = rgbToLab(otherColorString);

        double distance = Math.sqrt(Math.pow((ownLab[0] - otherLab[0]), 2) + Math.pow((ownLab[1] - otherLab[1]), 2) + Math.pow((ownLab[2] - otherLab[2]), 2));
        System.out.println(distance);

        if(distance > 25) {
            return true;
        } else {
            return false;
        }
    }

    public double[] rgbToLab(String hexString)
    {
        Color color = Color.valueOf(hexString);
        double[] rgb = new double[3];
        double[] xyz = new double[3];
        double[] lab = new double[3];

        rgb[0] = color.getRed();
        rgb[1] = color.getGreen();
        rgb[2] = color.getBlue();

        if (rgb[0] > 0.04045) {
            rgb[0] = Math.pow((rgb[0] + 0.055) / 1.055, 2.4);
        } else {
            rgb[0] = rgb[0] / 12.92;
        }

        if (rgb[1] > 0.04045) {
            rgb[1] = Math.pow((rgb[1] + 0.055) / 1.055, 2.4);
        } else {
            rgb[1] = rgb[1] / 12.92;
        }

        if (rgb[2] > 0.04045) {
            rgb[2] = Math.pow((rgb[2] + 0.055) / 1.055, 2.4);
        } else {
            rgb[2] = rgb[2] / 12.92;
        }

        rgb[0] = rgb[0] * 100.0;
        rgb[1] = rgb[1] * 100.0;
        rgb[2] = rgb[2] * 100.0;

        xyz[0] = ((rgb[0] * 0.412453) + (rgb[1] * 0.357580) + (rgb[2] * 0.180423));
        xyz[1] = ((rgb[0] * 0.212671) + (rgb[1] * 0.715160) + (rgb[2] * 0.072169));
        xyz[2] = ((rgb[0] * 0.019334) + (rgb[1] * 0.119193) + (rgb[2] * 0.950227));

        xyz[0] = xyz[0] / 95.047;
        xyz[1] = xyz[1] / 100.0;
        xyz[2] = xyz[2] / 108.883;

        if (xyz[0] > .008856) {
            xyz[0] = Math.pow(xyz[0], (1.0 / 3.0));
        } else {
            xyz[0] = (xyz[0] * 7.787) + (16.0 / 116.0);
        }

        if (xyz[1] > 0.008856) {
            xyz[1] = Math.pow(xyz[1], 1.0 / 3.0);
        } else {
            xyz[1] = (xyz[1] * 7.787) + (16.0 / 116.0);
        }

        if (xyz[2] > 0.008856) {
            xyz[2] = Math.pow(xyz[2], 1.0 / 3.0);
        } else {
            xyz[2] = (xyz[2] * 7.787) + (16.0 / 116.0);
        }

        lab[0] = (116.0 * xyz[1]) - 16.0;
        lab[1] = 500.0 * (xyz[0] - xyz[1]);
        lab[2] = 200.0 * (xyz[1] - xyz[2]);

        System.out.println("l: " + lab[0] + ", a: " + lab[1] + ", b: " +  lab[2]);
        return lab;
    }

    private PopupWindow getPopupWindow() {
        final ObservableList<Window> windows = Window.getWindows();
        final Iterator windowIterator = windows.iterator();
        while (windowIterator.hasNext()) {
            final Window window = (Window) windowIterator.next();
            if(window instanceof PopupWindow) {
                return (PopupWindow) window;
            }
        }
        return null;
    }
}
