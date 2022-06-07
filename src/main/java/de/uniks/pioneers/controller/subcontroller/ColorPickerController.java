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

public class ColorPickerController {
    private final ColorPicker colorPicker;
    private final SVGPath houseSVG;
    private String hexColor;
    private String[] pickerColors;
    private int colorIndex = 0;

    public ColorPickerController(ColorPicker colorPicker, SVGPath houseSVG)
    {
        this.colorPicker = colorPicker;
        this.houseSVG = houseSVG;
        this.houseSVG.setStroke(Paint.valueOf(colorPicker.getValue().toString()));
        this.initPickerColors();
        this.changePickerColors();
    }

    public void setColor() {
        this.hexColor = "#" + colorPicker.getValue().toString().substring(2,8);
        this.houseSVG.setStroke(Paint.valueOf(hexColor));
    }

    public void setColor(String hexColor) {
        this.hexColor = hexColor;
        this.colorPicker.setValue(Color.valueOf(hexColor));
        this.houseSVG.setStroke(Paint.valueOf(hexColor));
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

        return distance > 35;
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

        if(rgb[0] > 0.04045) {
            rgb[0] = Math.pow((rgb[0] + 0.055) / 1.055, 2.4);
        } else {
            rgb[0] = rgb[0] / 12.92;
        }

        if(rgb[1] > 0.04045) {
            rgb[1] = Math.pow((rgb[1] + 0.055) / 1.055, 2.4);
        } else {
            rgb[1] = rgb[1] / 12.92;
        }

        if(rgb[2] > 0.04045) {
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

        if(xyz[0] > 0.008856) {
            xyz[0] = Math.pow(xyz[0], (1.0 / 3.0));
        } else {
            xyz[0] = (xyz[0] * 7.787) + (16.0 / 116.0);
        }

        if(xyz[1] > 0.008856) {
            xyz[1] = Math.pow(xyz[1], 1.0 / 3.0);
        } else {
            xyz[1] = (xyz[1] * 7.787) + (16.0 / 116.0);
        }

        if(xyz[2] > 0.008856) {
            xyz[2] = Math.pow(xyz[2], 1.0 / 3.0);
        } else {
            xyz[2] = (xyz[2] * 7.787) + (16.0 / 116.0);
        }

        lab[0] = (116.0 * xyz[1]) - 16.0;
        lab[1] = 500.0 * (xyz[0] - xyz[1]);
        lab[2] = 200.0 * (xyz[1] - xyz[2]);

        return lab;
    }

    private PopupWindow getPopupWindow() {
        final ObservableList<Window> windows = Window.getWindows();
        for(Window window : windows) {
            if (window instanceof PopupWindow) {
                return (PopupWindow) window;
            }
        }
        return null;
    }

    private void initPickerColors() {
        pickerColors = new String[] {
                "#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#A4E804", "#008941", "#006FA6", "#A30059", "#3C3E6E",
                "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87", "#76912F",
                "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80", "#EA8B66",
                "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100", "#BCB1E5",
                "#DDEFFF", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F", "#DFFB71",
                "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09", "#E4FFFC",
                "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66", "#5EFF03",
                "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C", "#47675D",

                "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81", "#83A485",
                "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00", "#BE4700",
                "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700", "#9FA064",
                "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329", "#1A3A2A",
                "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#FF4A46", "#324E72", "#6A3A4C", "#B5D6C3",
                "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800", "#A38469",
                "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51", "#AA5199",
                "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58", "#F5E1FF"
        };
    }

    private void changePickerColors() {
        this.colorPicker.showingProperty().addListener((obs,b,b1) -> {
            if(b1) {
                PopupWindow popupWindow = getPopupWindow();
                assert popupWindow != null;
                Node popup = popupWindow.getScene().getRoot().getChildrenUnmodifiable().get(0);
                popup.lookupAll(".color-rect").forEach(rect -> {
                    // Replace with custom color
                    if(colorIndex < 132) {
                        ((Rectangle) rect).setFill(Color.valueOf(pickerColors[colorIndex]));
                        this.colorIndex += 1;
                    }
                });
            }
            this.colorIndex = 0;
        });
    }
}
