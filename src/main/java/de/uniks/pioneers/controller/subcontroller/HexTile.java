package de.uniks.pioneers.controller.subcontroller;

import static java.lang.Math.sqrt;

public class HexTile {
    public final int q;
    public final int r;
    public final int s;
    public final double x;
    public final double y;
    public String type = "";
    public int number;
    final double scale;

    public HexTile(int q, int r, int s, double scale, boolean top) {
        this.scale = scale;
        this.q = q;
        this.r = r;
        this.s = s;

        double[] kartCoords = cubeToCart(q, r, scale, top);
        x = kartCoords[0];
        y = kartCoords[1];
    }

    public void setGameInfo(String type, int number) {
        this.type = type;
        this.number = number;
    }

    private double[] cubeToCart(double q, double r, double scale, boolean top) {
        double[] coords = new double[2];

        if(top) {
            coords[0] = sqrt(3) * (r/2 + q) * scale;
            coords[1] = -(3.0/2.0) * r * scale;
        } else {
            coords[0] = -(3.0/2.0) * r * scale;
            coords[1] = sqrt(3) * (r/2 + q) * scale;
        }

        return coords;
    }

    @Override
    public String toString() {
        return q + " " + r + " " + s + " \tX: " + x + " Y: " +y;
    }
}
