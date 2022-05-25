package de.uniks.pioneers.controller.subcontroller;

import static de.uniks.pioneers.GameConstants.scale;

public class Tile {

    int q;
    int r;
    int s;

    public double x;
    public double y;

    String type = "";
    int number;

    public Tile(int q, int r, int s){

        this.q = q;
        this.r = r;
        this.s = s;

        double[] kartCoords = cubeToCart(q, r);
        x = kartCoords[0];
        y = kartCoords[1];

    }

    private double[] cubeToCart(double q, double r){

        double[] coords = new double[2];

        coords[0] = Math.sqrt(3) * (r/2 + q) * scale;
        coords[1] = -(3.0/2.0) * r * scale;

        return coords;
    }

    @Override
    public String toString(){

        return "X: " + this.x + "Y: " +this.y;

    }
}
